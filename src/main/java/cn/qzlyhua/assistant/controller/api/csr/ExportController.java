package cn.qzlyhua.assistant.controller.api.csr;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.qzlyhua.assistant.controller.api.response.Response;
import cn.qzlyhua.assistant.controller.api.response.ResponseData;
import cn.qzlyhua.assistant.dto.specification.Chapter;
import cn.qzlyhua.assistant.service.SpecificationService;
import cn.qzlyhua.assistant.util.word.Word2PdfAsposeUtil;
import com.aspose.words.SaveFormat;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.highlight.HighlightRenderPolicy;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.deepoove.poi.plugin.toc.TOCRenderPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 传输规范导出
 *
 * @author yanghua
 */
@RestController
@Response
@Slf4j
@RequestMapping("/api")
public class ExportController {
    @Resource
    SpecificationService specificationService;

    /**
     * 针对不同的浏览器进行文件名中文编码处理
     *
     * @param fileName
     * @param request
     * @return
     */
    public static String encodeFileName(String fileName, HttpServletRequest request) throws UnsupportedEncodingException {
        String agent = request.getHeader("USER-AGENT");
        if (agent != null && agent.contains("Mozilla")) {
            return new String(URLEncoder.encode(fileName, "UTF-8").getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        }
        return fileName;
    }

    /**
     * 导出word文档（按版本或业务领域）
     *
     * @param response
     * @throws IOException
     */
    @RequestMapping("/word/{fileName}")
    public void exportWordFile(@PathVariable String fileName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        fileName = fileName.toUpperCase(Locale.ROOT);
        List<Chapter> chapters = fileName.startsWith("PP") ? specificationService.getSpecificationsByVersion(fileName) :
                specificationService.getSpecificationsByBusinessArea(fileName);
        response.setContentType("application/msword");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodeFileName("传输规范-" + fileName, request) + ".docx");
        ServletOutputStream out = response.getOutputStream();

        Map<String, Object> map = new HashMap<>(4);
        map.put("version", fileName);
        map.put("today", DateUtil.format(new Date(), DatePattern.CHINESE_DATE_PATTERN));
        map.put("chapters", chapters);
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
                .useSpringEL()
                .build();

        XWPFTemplate xwpfTemplate = XWPFTemplate.compile(ResourceUtil.getStream("poi/template.docx"), config).render(map);
        xwpfTemplate.writeAndClose(out);
    }

    /**
     * 导出PDF文件：先根据单独的word模板文件生成docx文件，再转换成PDF文件
     * 需要服务器内安装所有字体，包括：微软雅黑、宋体、JetBrainsMono
     * 安装方法：复制字体文件至：/usr/share/fonts，执行命令：fc-cache -fv
     *
     * @param fileName
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("/pdf/{fileName}")
    public void exportPdfFile(@PathVariable String fileName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        fileName = fileName.toUpperCase(Locale.ROOT);
        List<Chapter> chapters = fileName.startsWith("PP") ? specificationService.getSpecificationsByVersion(fileName) :
                specificationService.getSpecificationsByBusinessArea(fileName);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodeFileName("传输规范-" + fileName, request) + ".pdf");

        Map<String, Object> map = new HashMap<>(3);
        map.put("version", fileName);
        map.put("today", DateUtil.format(new Date(), DatePattern.CHINESE_DATE_PATTERN));
        map.put("chapters", chapters);

        LoopRowTableRenderPolicy loopRowTableRenderPolicy = new LoopRowTableRenderPolicy();
        HighlightRenderPolicy highlightRenderPolicy = new HighlightRenderPolicy();
        Configure config = Configure.builder()
                .bind("reqParameters", loopRowTableRenderPolicy)
                .bind("resParameters", loopRowTableRenderPolicy)
                .bind("reqExample", highlightRenderPolicy)
                .bind("resExample", highlightRenderPolicy)
                .useSpringEL()
                .build();

        XWPFTemplate xwpfTemplate = XWPFTemplate.compile(ResourceUtil.getStream("poi/template-pdf.docx"), config).render(map);
        File tmpWordFile = File.createTempFile(fileName, ".docx");
        xwpfTemplate.writeToFile(tmpWordFile.getPath());
        File tmpPdfFile = File.createTempFile(fileName, ".pdf");
        Word2PdfAsposeUtil.doc2pdf(tmpWordFile.getPath(), tmpPdfFile.getPath(), SaveFormat.PDF);
        FileUtil.writeToStream(tmpPdfFile, response.getOutputStream());
    }

    /**
     * 导出MD文档（按版本）
     */
    @RequestMapping("/md/{version}")
    public ResponseData exportMdFile(@PathVariable String version) {
        Assert.isTrue(version.contains("PP"), "版本号规则有误！");
        List<Chapter> chapters = specificationService.getSpecificationsByVersion(version);
        specificationService.publishMarkDownFilesByVersion(chapters, version);
        // 对应业务领域的文档同步发布
        List<String> businessAreas = specificationService.getSpecificationsBusinessAreaByVersion(version);
        for (String bizName : businessAreas) {
            List<Chapter> cs = specificationService.getSpecificationsByBusinessArea(bizName);
            specificationService.publishMarkDownFilesByBusinessArea(cs, bizName);
        }
        return new ResponseData(200, version + "文档发布完成", null);
    }
}