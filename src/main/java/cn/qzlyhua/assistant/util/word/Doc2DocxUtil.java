package cn.qzlyhua.assistant.util.word;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.qzlyhua.assistant.dto.specification.Chapter;
import cn.qzlyhua.assistant.dto.specification.Parameter;
import cn.qzlyhua.assistant.dto.specification.Service;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.highlight.HighlightRenderData;
import com.deepoove.poi.plugin.highlight.HighlightRenderPolicy;
import com.deepoove.poi.plugin.highlight.HighlightStyle;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.deepoove.poi.plugin.toc.TOCRenderPolicy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * 03版Word文件（doc）处理工具类
 *
 * @author yanghua
 */
@Slf4j
public class Doc2DocxUtil {
    /**
     * 一级标题（业务领域）字号
     */
    private static final int BUS_AREA_LINE_SIZE = 32;

    /**
     * 二级标题（接口路径与名称）字号
     */
    private static final int PATH_LINE_SIZE = 28;

    /**
     * 基于接口文档（传输规范-PPxxx.doc）文件进行传输规范解析
     * 本地文件模式
     */
    public static CsrBook getAnalysisResult(File file, String version) throws IOException {
        Assert.isTrue(file.getName().endsWith("doc"), "仅支持03版的doc文件！");
        HWPFDocument document = new HWPFDocument(new POIFSFileSystem(file, true));
        List<TransmissionSpecification> res = analysis(document, version);
        return new CsrBook(res, null);
    }

    private static List<TransmissionSpecification> analysis(HWPFDocument document, String version) {
        // 得到文档的读取范围
        Range range = document.getRange();

        List<TransmissionSpecification> result = new ArrayList<>();
        String currentBusinessArea = null;
        String currentBusinessSubArea = null;
        String currentPath = null;
        String currentName = null;
        String currentDescription = null;
        String currentRemarks = null;
        List<TransmissionSpecificationParam> currentReqParams = null;
        String currentReqParamsExample = null;
        List<TransmissionSpecificationParam> currentResParams = null;
        String currentResParamsExample = null;

        for (int i = 0; i <= range.numParagraphs() - 1; i++) {
            // 获取第i段
            Paragraph paragraph = range.getParagraph(i);
            // 当前段落
            String paragraphText = paragraph.text().trim().replaceAll("\r\n", "");
            if (StrUtil.isNotBlank(paragraphText)) {
                CharacterRun characterRun = paragraph.getCharacterRun(0);
                log.info(i + "：\t(" + characterRun.getFontSize() + ")" + paragraphText);

                if (paragraphText.length() > 0 && !paragraphText.contains("HYPERLINK")) {
                    // 检测到一级标题行（业务领域）需要遵循字体格式
                    if (BUS_AREA_LINE_SIZE == characterRun.getFontSize()) {
                        // 遇到一级标题，若有历史数据，需要保存
                        flush(result, version, currentBusinessArea, currentBusinessSubArea, currentPath, currentName, currentDescription, currentRemarks,
                                currentReqParams, currentReqParamsExample, currentResParams, currentResParamsExample);
                        currentPath = null;

                        paragraphText = paragraphText.trim().replaceAll("\\(", "（").replaceAll("\\)", "）");
                        if (paragraphText.contains("、")) {
                            paragraphText = paragraphText.split("、")[1];
                        }

                        if (paragraphText.contains("（") && paragraphText.endsWith("）")) {
                            currentBusinessArea = paragraphText.split("（")[0];
                            currentBusinessSubArea = paragraphText.split("（")[1].replace("）", "");
                        } else {
                            currentBusinessArea = paragraphText;
                            currentBusinessSubArea = paragraphText;
                        }
                    }
                    // 检测到二级标题行（方法名）需要遵循字体格式
                    else if (PATH_LINE_SIZE == characterRun.getFontSize()) {
                        paragraphText = paragraphText.trim().replaceAll("\\(", "（").replaceAll("\\)", "）").replaceAll("  ", " ");
                        if (!paragraphText.contains("（") && paragraphText.contains(" ")) {
                            paragraphText = paragraphText.split(" ")[0] + "（" + paragraphText.split(" ")[1] + "）";
                        }

                        if (paragraphText.contains("（") && paragraphText.endsWith("）")) {
                            // 遇到二级标题，若有历史数据，需要保存
                            flush(result, version, currentBusinessArea, currentBusinessSubArea, currentPath, currentName, currentDescription, currentRemarks,
                                    currentReqParams, currentReqParamsExample, currentResParams, currentResParamsExample);

                            currentPath = paragraphText.split("（")[0];
                            currentPath = currentPath.startsWith("/") ? currentPath.substring(1) : currentPath;
                            currentName = paragraphText.split("（")[1].replace("）", "");

                            // 清空相关属性
                            currentDescription = null;
                            currentRemarks = null;
                            currentReqParams = null;
                            currentReqParamsExample = null;
                            currentResParams = null;
                            currentResParamsExample = null;
                        }
                    } else if (paragraphText.startsWith("功能：")) {
                        currentDescription = paragraphText.replace("功能：", "");
                    } else if (paragraphText.startsWith("说明：")) {
                        currentRemarks = paragraphText.replace("说明：", "");
                    } else if (paragraphText.startsWith("入参：") && !"入参：无".equals(paragraphText)) {
                        // 循环往下，解析入参
                        ParamTableInfo paramTableInfo = getTableInfo(range, i);
                        currentReqParams = paramTableInfo.getTableInfo();
                        i = paramTableInfo.getNextLineNumber();
                    } else if (paragraphText.startsWith("出参：") && !"出参：无".equals(paragraphText)) {
                        // 循环往下，解析出参
                        ParamTableInfo paramTableInfo = getTableInfo(range, i);
                        currentResParams = paramTableInfo.getTableInfo();
                        i = paramTableInfo.getNextLineNumber();
                    } else if (paragraphText.startsWith("入参举例：")) {
                        // 循环往下，解析入参举例
                        JsonTableInfo jsonTableInfo = getJsonTableInfo(range, i);
                        currentReqParamsExample = jsonTableInfo.getJsonString();
                        i = jsonTableInfo.getNextLineNumber();
                    } else if (paragraphText.startsWith("出参举例：")) {
                        // 循环往下，解析出参举例
                        JsonTableInfo jsonTableInfo = getJsonTableInfo(range, i);
                        currentResParamsExample = jsonTableInfo.getJsonString();
                        i = jsonTableInfo.getNextLineNumber();
                    }
                }
            }
        }

        // 提交最后一个接口
        flush(result, version, currentBusinessArea, currentBusinessSubArea, currentPath, currentName, currentDescription, currentRemarks,
                currentReqParams, currentReqParamsExample, currentResParams, currentResParamsExample);
        return result;
    }

    /**
     * 将读取到的接口信息保存至List<TransmissionSpecification>对象内
     *
     * @param result
     * @param version
     * @param currentBusinessArea
     * @param currentPath
     * @param currentName
     * @param currentDescription
     * @param currentRemarks
     * @param currentReqParams
     * @param currentReqParamsExample
     * @param currentResParams
     * @param currentResParamsExample
     */
    public static void flush(List<TransmissionSpecification> result,
                             String version, String currentBusinessArea, String currentBusinessSubArea,
                             String currentPath, String currentName, String currentDescription, String currentRemarks,
                             List<TransmissionSpecificationParam> currentReqParams, String currentReqParamsExample,
                             List<TransmissionSpecificationParam> currentResParams, String currentResParamsExample) {
        if (StrUtil.isNotBlank(currentPath)) {
            result.add(TransmissionSpecification.builder()
                    .businessArea(currentBusinessArea)
                    .businessSubArea(currentBusinessSubArea)
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
                String key = range.getParagraph(line++).text().trim().replaceAll(" ", "").replaceAll("\r\n", "");
                String type = range.getParagraph(line++).text().trim().replaceAll("\r\n", "");
                StringBuilder des = new StringBuilder(range.getParagraph(line++).text().trim().replaceAll("\r\n", ""));
                String required = range.getParagraph(line++).text().trim().replaceAll("\r\n", "");

                log.info("[T]" + line + "\t" + key);
                // 简易校验
                if (StrUtil.isNotBlank(key) && ReUtil.isMatch(PatternPool.WORD, key.replaceAll("\\.", "").replaceAll("_", ""))) {
                    // 兼容[描述]单元格内出现换行的场景
                    while (!"Y".equals(required) && !"N".equals(required) && !"".equals(required)) {
                        des.append("\n").append(required);
                        required = range.getParagraph(line++).text().trim().replaceAll("\r\n", "");

                        String testLineText = range.getParagraph(line + 1).text().trim().replaceAll("\r\n", "");
                        if (!"Y".equals(testLineText) && !"N".equals(testLineText) &&
                                ReUtil.isMatch(PatternPool.WORD, testLineText.replaceAll("\\.", "").replaceAll("_", ""))) {
                            break;
                        }
                    }

                    result.add(TransmissionSpecificationParam.builder()
                            .key(key)
                            .type(type.toUpperCase(Locale.ROOT))
                            .describe(des.toString())
                            .required(required.toUpperCase(Locale.ROOT))
                            .build());
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

    public static void main(String[] args) throws IOException {
        String path = "/Users/yanghua/Desktop/传输规范-基层公卫/基层公卫-PEM(健康体检).doc";
        String pathTo = "/Users/yanghua/Desktop/传输规范-基层公卫/基层公卫-PEM(健康体检).docx";
        String version = "PP101";
        CsrBook csrBook = getAnalysisResult(FileUtil.file(path), version);
        List<TransmissionSpecification> list = csrBook.getTransmissionSpecifications();

        List<Chapter> chapters = new ArrayList<>();
        Set<String> businessAreas = new LinkedHashSet<>();

        for (TransmissionSpecification t : list) {
            businessAreas.add(t.getBusinessArea());
        }
        for (String bizAreaName : businessAreas) {
            List<Service> services = new ArrayList<>();
            for (TransmissionSpecification a : list) {
                if (bizAreaName.equals(a.getBusinessArea())) {
                    List<Parameter> reqParameters = new ArrayList<>();
                    List<Parameter> resParameters = new ArrayList<>();

                    if (CollUtil.isNotEmpty(a.getReqParams())) {
                        for (TransmissionSpecificationParam s : a.getReqParams()) {
                            reqParameters.add(Parameter.builder()
                                    .key(s.getKey())
                                    .des(s.getDescribe())
                                    .type(s.getType())
                                    .isRequired(s.getRequired()).build());
                        }
                    }

                    if (CollUtil.isNotEmpty(a.getResParams())) {
                        for (TransmissionSpecificationParam s : a.getResParams()) {
                            resParameters.add(Parameter.builder()
                                    .key(s.getKey())
                                    .des(s.getDescribe())
                                    .type(s.getType())
                                    .isRequired(s.getRequired()).build());
                        }
                    }

                    Service service = Service.builder()
                            .serviceName(a.getPath())
                            .serviceNick(a.getName())
                            .version(a.getVersion())
                            .businessArea(a.getBusinessArea())
                            .description(StrUtil.isBlank(a.getDescription()) ? a.getName() : a.getDescription())
                            .explain(a.getRemarks() == null ? "无" : a.getRemarks())
                            .reqParameters(reqParameters)
                            .reqExampleStr(prettyJson(a.getReqParamsExample()))
                            .reqExample(getHighlightRenderData(a.getReqParamsExample()))
                            .resParameters(resParameters)
                            .resExampleStr(prettyJson(a.getResParamsExample()))
                            .resExample(getHighlightRenderData(a.getResParamsExample()))
                            .dictionaries(new ArrayList<>()).build();
                    services.add(service);

                }
            }

            Chapter chapter = Chapter.builder()
                    .headWord(bizAreaName)
                    .services(services).build();
            chapters.add(chapter);
        }

        Map<String, Object> map = new HashMap<>(4);
        map.put("version", version);
        map.put("today", DateUtil.format(new Date(), DatePattern.CHINESE_DATE_PATTERN));
        map.put("chapters", chapters);
        map.put("dictionaries", new ArrayList<>());
        map.put("needDic", false);
        // 目录，打开word文件后会提醒生成目录
        map.put("toc", "");

        LoopRowTableRenderPolicy loopRowTableRenderPolicy = new LoopRowTableRenderPolicy();
        HighlightRenderPolicy highlightRenderPolicy = new HighlightRenderPolicy();
        Configure config = Configure.builder()
                .bind("reqParameters", loopRowTableRenderPolicy)
                .bind("resParameters", loopRowTableRenderPolicy)
                .bind("reqExample", highlightRenderPolicy)
                .bind("resExample", highlightRenderPolicy)
                .bind("toc", new TOCRenderPolicy())
                .bind("dictionaries", new CsrDictionariesTablePolicy())
                .useSpringEL()
                .build();

        XWPFTemplate xwpfTemplate = XWPFTemplate.compile(ResourceUtil.getStream("poi/template.docx"), config).render(map);
        xwpfTemplate.writeAndClose(new FileOutputStream(pathTo));
    }

    private static String prettyJson(String json) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object obj = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // HuTool 方案。不校验JSON格式。
            return JSONUtil.formatJsonStr(json);
        }
    }

    private static HighlightRenderData getHighlightRenderData(String code) {
        HighlightRenderData source = new HighlightRenderData();
        if (StrUtil.isNotBlank(code) && code.trim().startsWith("curl")) {
            source.setCode(code);
            source.setLanguage("bash");
        } else {
            // 不能返回空，会导致渲染时报错
            source.setCode(StrUtil.isNotBlank(code) ? prettyJson(code) : "{\"_field\":\"_value\"}");
            source.setLanguage("json");
        }
        source.setStyle(HighlightStyle.builder()
                .withShowLine(false)
                .withFontFamily("Consolas")
                .withTheme("default").build());
        return source;
    }
}
