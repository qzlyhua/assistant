package cn.qzlyhua.assistant.util;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.qzlyhua.assistant.util.word.TransmissionSpecification;
import cn.qzlyhua.assistant.util.word.TransmissionSpecificationParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Word操作工具类
 *
 * @author yanghua
 */
@Slf4j
public class WordUtil {
    /**
     * 一级标题（业务领域）字号
     */
    private static final int BUS_AREA_LINE_SIZE = 32;

    /**
     * 二级标题（接口路径与名称）字号
     */
    private static final int PATH_LINE_SIZE = 28;

    public static List<TransmissionSpecification> getAnalysisResult(String wordFilePath, String version) {
        Assert.isTrue(wordFilePath.endsWith("doc"), "仅支持03版的doc文件！");
        Assert.isTrue(new File(wordFilePath).exists(), "word文件不存在！");

        try {
            HWPFDocument document = new HWPFDocument(new POIFSFileSystem(new FileInputStream(wordFilePath)));
            // 得到文档的读取范围
            Range range = document.getRange();

            List<TransmissionSpecification> result = new ArrayList<>();
            String currentBusinessArea = null;
            String currentPath = null;
            String currentName = null;
            String currentDescription = null;
            String currentRemarks = null;
            List<TransmissionSpecificationParam> currentReqParams = null;
            String currentReqParamsExample = null;
            List<TransmissionSpecificationParam> currentResParams = null;
            String currentResParamsExample = null;

            for (int i = 0; i < range.numParagraphs() - 1; i++) {
                // 获取第i段
                Paragraph paragraph = range.getParagraph(i);
                // 当前段落
                String paragraphText = paragraph.text().trim().replaceAll("\r\n", "");
                if (StrUtil.isNotBlank(paragraphText)) {
                    log.info(i + "：\t" + paragraphText);
                    CharacterRun characterRun = paragraph.getCharacterRun(0);
                    if (paragraphText.length() > 0 && !paragraphText.contains("HYPERLINK")) {
                        if (BUS_AREA_LINE_SIZE == characterRun.getFontSize()) {
                            currentBusinessArea = paragraphText;
                        }

                        if (PATH_LINE_SIZE == characterRun.getFontSize() && paragraphText.contains("（") && paragraphText.endsWith("）")) {
                            if (StrUtil.isNotBlank(currentPath)) {
                                result.add(TransmissionSpecification.builder()
                                        .businessArea(currentBusinessArea)
                                        .version(version)
                                        .path(currentPath)
                                        .name(currentName)
                                        .description(currentDescription)
                                        .remarks(currentRemarks)
                                        .reqParams(currentReqParams)
                                        .reqParamsExample(currentReqParamsExample)
                                        .resParams(currentResParams)
                                        .resParamsExample(currentResParamsExample)
                                        .build());

                                // 清空相关属性，进行新的接口描述
                                currentDescription = null;
                                currentRemarks = null;
                                currentReqParams = null;
                                currentReqParamsExample = null;
                                currentResParams = null;
                                currentResParamsExample = null;
                            }

                            currentPath = paragraphText.split("（")[0];
                            currentPath = currentPath.startsWith("/") ? currentName.substring(1) : currentPath;
                            currentName = paragraphText.split("（")[1].replace("）", "");
                        }

                        if (characterRun.getFontSize() < PATH_LINE_SIZE && paragraphText.startsWith("功能：")) {
                            currentDescription = paragraphText.replace("功能：", "");
                        }

                        if (characterRun.getFontSize() < PATH_LINE_SIZE && paragraphText.startsWith("说明：")) {
                            currentRemarks = paragraphText.replace("说明：", "");
                        }

                        if (characterRun.getFontSize() < PATH_LINE_SIZE && paragraphText.startsWith("入参：")) {
                            // 循环往下，解析入参
                            ParamTableInfo paramTableInfo = getTableInfo(range, i);
                            currentReqParams = paramTableInfo.getTableInfo();
                            i = paramTableInfo.getNextLineNumber();
                        }

                        if (characterRun.getFontSize() < PATH_LINE_SIZE && paragraphText.startsWith("出参：")) {
                            // 循环往下，解析出参
                            ParamTableInfo paramTableInfo = getTableInfo(range, i);
                            currentResParams = paramTableInfo.getTableInfo();
                            i = paramTableInfo.getNextLineNumber();
                        }

                        if (characterRun.getFontSize() < PATH_LINE_SIZE && paragraphText.startsWith("入参举例：")) {
                            // 循环往下，解析入参举例
                            JsonTableInfo jsonTableInfo = getJsonTableInfo(range, i);
                            currentReqParamsExample = jsonTableInfo.getJsonString();
                            i = jsonTableInfo.getNextLineNumber();
                        }

                        if (characterRun.getFontSize() < PATH_LINE_SIZE && paragraphText.startsWith("出参举例：")) {
                            // 循环往下，解析出参举例
                            JsonTableInfo jsonTableInfo = getJsonTableInfo(range, i);
                            currentResParamsExample = jsonTableInfo.getJsonString();
                            i = jsonTableInfo.getNextLineNumber();
                        }
                    }
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 读取出入参举例json字符串
     * 直到读取到关键行（接口名称标题行或入参举例标题或出参举例标题）
     *
     * @param range
     * @param i
     * @return
     */
    private static JsonTableInfo getJsonTableInfo(Range range, int i) {
        int nextLoopLine = i;
        int line = i;
        String result = "";

        while (line < range.numParagraphs() - 1) {
            Paragraph paragraph = range.getParagraph(++line);

            // 当前段落
            String text = paragraph.text().trim().replaceAll("\r\n", "");
            CharacterRun characterRun = paragraph.getCharacterRun(0);

            if (!text.contains("出参举例") && !text.contains("入参举例") && characterRun.getFontSize() < PATH_LINE_SIZE) {
                result += text;
                // 标记跳出该获取table信息内参数的方法后，下一次读取的行数
                nextLoopLine = line;
            } else {
                break;
            }
        }

        return new JsonTableInfo(nextLoopLine, result.trim().replaceAll("\\u00a0", ""));
    }

    /**
     * 读取table内的出入参描述信息
     *
     * @param range
     * @param i
     * @return
     */
    private static ParamTableInfo getTableInfo(Range range, int i) {
        int nextLoopLine = i;
        int line = i;
        String nextLineText = range.getParagraph(++line).text().trim().replaceAll("\r\n", "");
        // 通过表头测试是否为预料中的表格
        if ("属性名".equals(nextLineText)) {
            List<TransmissionSpecificationParam> result = new ArrayList<>();

            line = line + 5;

            while (line < range.numParagraphs() - 1) {
                // 获取表格中的1行
                String key = range.getParagraph(line++).text().trim().replaceAll("\r\n", "");
                String type = range.getParagraph(line++).text().trim().replaceAll("\r\n", "");
                String des = range.getParagraph(line++).text().trim().replaceAll("\r\n", "");
                String required = range.getParagraph(line++).text().trim().replaceAll("\r\n", "");

                // 简易校验
                if (StrUtil.isNotBlank(key) && ReUtil.isMatch(PatternPool.WORD, key.replaceAll("\\.", ""))) {
                    result.add(TransmissionSpecificationParam.builder()
                            .key(key)
                            .type(type)
                            .describe(des)
                            .required(required).build());
                    // 标记跳出该获取table信息内参数的方法后，下一次读取的行数
                    nextLoopLine = line;
                    // 表格当前row的换行，读取下一行数据（或其他内容）
                    line++;
                } else {
                    break;
                }
            }
            return new ParamTableInfo(nextLoopLine, result);
        } else {
            return new ParamTableInfo(nextLoopLine, null);
        }
    }

    public static void main(String[] args) {
        String wordPath = "/Users/yanghua/Downloads/传输规范-PP012.doc";

        List<TransmissionSpecification> list = getAnalysisResult(wordPath, "PP012");
        log.info(JSONUtil.toJsonStr(list));
    }

    @Data
    @AllArgsConstructor
    private static class ParamTableInfo {
        private int nextLineNumber;
        private List<TransmissionSpecificationParam> tableInfo;
    }

    @Data
    @AllArgsConstructor
    private static class JsonTableInfo {
        private int nextLineNumber;
        private String jsonString;
    }
}
