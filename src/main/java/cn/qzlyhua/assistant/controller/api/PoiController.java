package cn.qzlyhua.assistant.controller.api;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.qzlyhua.assistant.controller.api.response.Response;
import cn.qzlyhua.assistant.dto.specification.Chapter;
import cn.qzlyhua.assistant.service.SpecificationService;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.highlight.HighlightRenderPolicy;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.deepoove.poi.plugin.toc.TOCRenderPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
 * MySQL数据库操作
 *
 * @author yanghua
 */
@RestController
@Response
@Slf4j
@RequestMapping("/api")
public class PoiController {
    @Resource
    SpecificationService specificationService;

    /**
     * SpecificationService
     * 测试POI生成word文件
     *
     * @param response
     * @throws IOException
     */
    @RequestMapping("/poi/{fileName}")
    public void exportWordFile(@PathVariable String fileName, HttpServletRequest request, HttpServletResponse response) throws IOException {
        fileName = fileName.toUpperCase(Locale.ROOT);
        List<Chapter> chapters = fileName.startsWith("PP") ? specificationService.getSpecificationsByVersion(fileName) :
                specificationService.getSpecificationsByBusinessArea(fileName);
        response.setContentType("application/msword");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodeFileName("传输规范-" + fileName, request) + ".docx");
        ServletOutputStream out = response.getOutputStream();

        Map<String, Object> map = new HashMap<>(10);
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

    @PostMapping("/poi/import/{v}")
    public void importWordFile(@PathVariable String v, MultipartFile file) throws IOException {
        specificationService.importSpecificationsFromWord(file, v.toUpperCase(Locale.ROOT));
    }

    /**
     * 针对不同的浏览器进行文件名中文编码处理
     *
     * @param fileName
     * @param request
     * @return
     */
    public static String encodeFileName(String fileName, HttpServletRequest request) {
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            String agent = request.getHeader("USER-AGENT");
            if (agent != null && agent.contains("Mozilla")) {
                fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fileName;
    }
}