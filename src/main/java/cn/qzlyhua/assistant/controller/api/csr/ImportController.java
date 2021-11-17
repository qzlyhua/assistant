package cn.qzlyhua.assistant.controller.api.csr;

import cn.qzlyhua.assistant.controller.api.response.Response;
import cn.qzlyhua.assistant.controller.api.response.ResponseData;
import cn.qzlyhua.assistant.dto.specification.Chapter;
import cn.qzlyhua.assistant.service.SpecificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 传输规范导入
 *
 * @author yanghua
 */
@RestController
@Response
@Slf4j
@RequestMapping("/api")
public class ImportController {
    @Resource
    SpecificationService specificationService;

    @PostMapping("/poi/import")
    public ResponseData importWordFile(MultipartFile file) throws IOException {
        String version = getVersionFromFile(file);
        // 入库
        int res = specificationService.importSpecificationsFromWord(file, version);
        return new ResponseData(200, "成功导入" + res + "条", res);
    }

    @PostMapping("/poi/importAndPublish")
    public ResponseData importWordFilAndPublishMarkdown(MultipartFile file) throws IOException {
        Date now = new Date();
        String version = getVersionFromFile(file);
        // 入库
        int res = specificationService.importSpecificationsFromWord(file, version);
        // 发布在该时间点之后导入的传输规范-业务领域
        List<String> businessAreas = specificationService.getSpecificationsBusinessAreaByUpdateTime(now);
        for (String bizName : businessAreas) {
            List<Chapter> chapters = specificationService.getSpecificationsByBusinessArea(bizName);
            specificationService.publishMarkDownFilesByBusinessArea(chapters, bizName);
        }

        // 发布在该时间点之后导入的传输规范-版本迭代
        List<Chapter> chapters = specificationService.getSpecificationsByVersion(version);
        specificationService.publishMarkDownFilesByVersion(chapters, version);

        return new ResponseData(200, "成功导入并发布" + res + "条", res);
    }

    private String getVersionFromFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename().toUpperCase(Locale.ROOT);
        Assert.isTrue(originalFilename.startsWith("传输规范-"), "文件命名格式：传输规范-PP0XX.docx");
        String version = originalFilename.substring(originalFilename.indexOf("-") + 1, originalFilename.lastIndexOf("."));
        return version;
    }
}