package cn.qzlyhua.assistant.controller.api;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.qzlyhua.assistant.controller.api.response.Response;
import cn.qzlyhua.assistant.dto.specification.Chapter;
import cn.qzlyhua.assistant.service.SpecificationService;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.policy.HackLoopTableRenderPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void poi(@PathVariable String fileName, HttpServletResponse response) throws IOException {
        List<Chapter> chapters = specificationService.getSpecifications();
        response.setContentType("application/msword");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".docx");
        ServletOutputStream out = response.getOutputStream();

        Map<String, Object> map = new HashMap<>(10);
        map.put("version", "PP008");
        map.put("today", DateUtil.format(new Date(), DatePattern.CHINESE_DATE_PATTERN));
        map.put("author", "杨骅");
        map.put("chapters", chapters);

        HackLoopTableRenderPolicy hackLoopTableRenderPolicy = new HackLoopTableRenderPolicy();
        Configure config = Configure.builder()
                .bind("chapters", hackLoopTableRenderPolicy)
                .bind("services", hackLoopTableRenderPolicy)
                .bind("reqParameters", hackLoopTableRenderPolicy)
                .bind("resParameters", hackLoopTableRenderPolicy)
                .useSpringEL()
                .build();

        XWPFTemplate xwpfTemplate = XWPFTemplate.compile(ResourceUtil.getStream("poi/template.docx"), config).render(map);
        xwpfTemplate.writeAndClose(out);
    }
}