package cn.qzlyhua.assistant.controller.api.csr;

import cn.qzlyhua.assistant.controller.api.response.Response;
import cn.qzlyhua.assistant.controller.api.response.ResponseData;
import cn.qzlyhua.assistant.service.SpecificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Locale;

/**
 * MySQL数据库操作
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
        String originalFilename = file.getOriginalFilename().toUpperCase(Locale.ROOT);
        Assert.isTrue(originalFilename.contains("PP") && originalFilename.contains("-"), "仅支持按版本导入，文件命名格式：传输规范-PP0XX.docx");
        String version = originalFilename.substring(originalFilename.indexOf("-") + 1, originalFilename.lastIndexOf("."));
        int res = specificationService.importSpecificationsFromWord(file, version);
        return new ResponseData(200, "成功导入" + res + "条", res);
    }
}