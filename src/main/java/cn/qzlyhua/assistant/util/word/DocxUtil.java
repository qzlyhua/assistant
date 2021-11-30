package cn.qzlyhua.assistant.util.word;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 07版Word文件（docx）处理工具类
 *
 * @author yanghua
 */
public class DocxUtil {
    private static final String TABLE_REQ_PARAM = "TABLE_REQ_PARAM";
    private static final String TABLE_RES_PARAM = "TABLE_RES_PARAM";
    private static final String TABLE_REQ_EXAMPLE = "TABLE_REQ_EXAMPLE";
    private static final String TABLE_RES_EXAMPLE = "TABLE_RES_EXAMPLE";
    private static final String TABLE_DIC = "TABLE_DIC";

    public static CsrBook getAnalysisResult(MultipartFile file, String version) throws IOException {
        Assert.isTrue(file.getOriginalFilename().endsWith("docx"), "仅支持07版的docx文件！");
        InputStream inputStream = file.getInputStream();
        XWPFDocument document = new XWPFDocument(inputStream);
        CsrBook res = analysis(document, version);
        IoUtil.close(inputStream);
        return res;
    }

    public static CsrBook getAnalysisResult(File file, String version) throws IOException {
        Assert.isTrue(file.getName().endsWith("docx"), "仅支持07版的docx文件！");
        BufferedInputStream inputStream = FileUtil.getInputStream(file);
        XWPFDocument document = new XWPFDocument(inputStream);
        CsrBook res = analysis(document, version);
        IoUtil.close(inputStream);
        return res;
    }

    private static CsrBook analysis(XWPFDocument document, String version) {
        List<TransmissionSpecification> transmissionSpecifications = new ArrayList<>();
        List<Dictionary> dictionaries = new ArrayList<>();

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

        // 标记当前解析到的table的类型
        String currentTableType = "";

        List<IBodyElement> bodyElements = document.getBodyElements();
        for (int i = 0; i < bodyElements.size() - 1; i++) {
            IBodyElement element = bodyElements.get(i);
            if (element.getElementType().equals(BodyElementType.PARAGRAPH)) {
                XWPFParagraph paragraph = (XWPFParagraph) element;
                String paragraphText = paragraph.getText().trim().replaceAll("\r\n", "");

                // 段落编号：【%1、】【%1.%2】【】
                String numLevelText = paragraph.getNumLevelText();
                // 段落样式：【3】【5】【afb】
                String style = paragraph.getStyle();

                System.out.println("[" + numLevelText + "][" + style + "] \t" + paragraphText);

                // 检测到一级标题行（业务领域）
                if ("%1、".equals(numLevelText) || "3".equals(style)) {
                    // 遇到一级标题，若有历史数据，需要保存
                    DocUtil.flush(transmissionSpecifications, version, currentBusinessArea, currentBusinessSubArea,
                            currentPath, currentName, currentDescription, currentRemarks,
                            currentReqParams, currentReqParamsExample, currentResParams, currentResParamsExample);

                    currentPath = null;

                    if (paragraphText.contains("（") && paragraphText.endsWith("）")) {
                        currentBusinessArea = paragraphText.split("（")[0];
                        currentBusinessSubArea = paragraphText.split("（")[1].replace("）", "");
                    } else {
                        currentBusinessArea = paragraphText;
                        currentBusinessSubArea = paragraphText;
                    }
                }
                // 检测到二级标题行（方法名）需要遵循字体格式
                else if ("%1.%2".equals(numLevelText) || "5".equals(style)) {
                    paragraphText = paragraphText.replaceAll("\\(", "（").replaceAll("\\)", "）");
                    if (paragraphText.contains("（") && paragraphText.endsWith("）")) {
                        // 遇到二级标题，若有历史数据，需要保存
                        DocUtil.flush(transmissionSpecifications, version, currentBusinessArea, currentBusinessSubArea,
                                currentPath, currentName, currentDescription, currentRemarks,
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
                    currentTableType = TABLE_REQ_PARAM;
                } else if (paragraphText.startsWith("出参：") && !"出参：无".equals(paragraphText)) {
                    currentTableType = TABLE_RES_PARAM;
                } else if (paragraphText.startsWith("入参举例：")) {
                    currentTableType = TABLE_REQ_EXAMPLE;
                } else if (paragraphText.startsWith("出参举例：")) {
                    currentTableType = TABLE_RES_EXAMPLE;
                } else if ("数据字典".equals(paragraphText)) {
                    currentTableType = TABLE_DIC;
                }
            } else if (element.getElementType().equals(BodyElementType.TABLE)) {
                XWPFTable table = (XWPFTable) element;
                if (TABLE_REQ_PARAM.equals(currentTableType)) {
                    currentReqParams = getParamFromTable(table);
                    currentTableType = "";
                } else if (TABLE_RES_PARAM.equals(currentTableType)) {
                    currentResParams = getParamFromTable(table);
                    currentTableType = "";
                } else if (TABLE_REQ_EXAMPLE.equals(currentTableType)) {
                    currentReqParamsExample = getJsonStrFromTable(table);
                    currentTableType = "";
                } else if (TABLE_RES_EXAMPLE.equals(currentTableType)) {
                    currentResParamsExample = getJsonStrFromTable(table);
                    currentTableType = "";
                } else if (TABLE_DIC.equals(currentTableType)) {
                    dictionaries = getDictionariesFromTable(table);
                    currentTableType = "";
                }
            }
        }

        // 提交最后一个接口
        DocUtil.flush(transmissionSpecifications, version, currentBusinessArea, currentBusinessSubArea,
                currentPath, currentName, currentDescription, currentRemarks,
                currentReqParams, currentReqParamsExample, currentResParams, currentResParamsExample);

        return new CsrBook(transmissionSpecifications, dictionaries);
    }


    private static List<TransmissionSpecificationParam> getParamFromTable(XWPFTable table) {
        if (table.getRows().size() >= 2 && "属性名".equals(table.getRows().get(0).getTableCells().get(0).getText())) {
            List<TransmissionSpecificationParam> res = new ArrayList<>();
            for (int i = 1; i <= table.getRows().size() - 1; i++) {
                List<XWPFTableCell> cells = table.getRow(i).getTableCells();
                res.add(TransmissionSpecificationParam.builder()
                        .key(cells.get(0).getText())
                        .type(cells.get(1).getText())
                        .describe(cells.get(2).getText())
                        .required(cells.get(3).getText())
                        .build());
            }
            return res;
        } else {
            return null;
        }
    }

    private static List<Dictionary> getDictionariesFromTable(XWPFTable table) {
        if (table.getRows().size() >= 2 && "字典类别".equals(table.getRows().get(0).getTableCells().get(0).getText())) {
            List<Dictionary> res = new ArrayList<>();
            String currentDicType = "";
            for (int i = 1; i <= table.getRows().size() - 1; i++) {
                List<XWPFTableCell> cells = table.getRow(i).getTableCells();

                XWPFTableCell firstCell = cells.get(0);
                CTVMerge vMerge = firstCell.getCTTc().getTcPr().getVMerge();
                if (vMerge == null || (vMerge != null && vMerge.getVal() != null)) {
                    currentDicType = firstCell.getText();
                }
                res.add(Dictionary.builder()
                        .type(currentDicType)
                        .code(cells.get(1).getText())
                        .name(cells.get(2).getText())
                        .build());
            }
            return res;
        } else {
            return null;
        }
    }

    private static String getJsonStrFromTable(XWPFTable table) {
        String def = "{\"_demo_field\":\"_demo_value\"}";
        if (table.getRows().size() >= 1) {
            String json = table.getRows().get(0).getTableCells().get(0).getText().trim().replaceAll("\\u00a0", "");
            return StrUtil.isBlank(json) ? def : json;
        } else {
            return def;
        }
    }

    public static void main(String[] args) throws Exception {
        String version = "PP003";
        String docx = "/Users/yanghua/SVN/研发文档库/接口规范/迭代接口/传输规范-" + version + ".docx";
        CsrBook book = getAnalysisResult(FileUtil.file(docx), version);
        System.out.println(book);
    }
}
