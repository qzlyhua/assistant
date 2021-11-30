package cn.qzlyhua.assistant.controller.api.csr;

import cn.hutool.core.collection.CollUtil;
import cn.qzlyhua.assistant.controller.api.response.Response;
import cn.qzlyhua.assistant.controller.api.response.ResponseData;
import cn.qzlyhua.assistant.dto.csr.message.NoticeForChange;
import cn.qzlyhua.assistant.dto.specification.Chapter;
import cn.qzlyhua.assistant.entity.ApiCsr;
import cn.qzlyhua.assistant.entity.ApiCsrParam;
import cn.qzlyhua.assistant.service.SpecificationService;
import cn.qzlyhua.assistant.util.DingTalkSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @PostMapping("/poi/importAndPublishQuietly")
    public ResponseData importAndPublishQuietly(MultipartFile file) throws IOException {
        return importWordFilAndPublishMarkdown(file, "quietly");
    }

    @PostMapping("/poi/importAndPublish")
    public ResponseData importWordFilAndPublishMarkdown(MultipartFile file, @RequestParam(required = false) String quietly) throws IOException {
        // 时间标识，用于后续更新该时间点后发布的传输规范对应的在线文档
        Date now = new Date();

        String version = getVersionFromFile(file);
        List<ApiCsr> origApiCsrs = specificationService.getApiCsrsByVersion(version);
        List<ApiCsrParam> origApiCsrParams = specificationService.getApiCsrParamsByVersion(version);

        // 删除主表及参数
        if (CollUtil.isNotEmpty(origApiCsrs)) {
            specificationService.deleteAllByVersion(version);
        }

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

        NoticeForChange noticeForChange = specificationService.getNoticeForChange(version, origApiCsrs, origApiCsrParams);

        if (noticeForChange.isNewVersion()
                || CollUtil.isNotEmpty(noticeForChange.getInterfaceAdded())
                || CollUtil.isNotEmpty(noticeForChange.getInterfaceEdited())
                || CollUtil.isNotEmpty(noticeForChange.getInterfaceDeleted())) {
            specificationService.publishMarkDownFilesOfChangelog(noticeForChange);

            if (!"quietly".equals(quietly)) {
                // 检查新发布的内容与原有内容的变更情况，并发送钉钉通知
                sendDingTalkMessage(noticeForChange);
            }
        }

        return new ResponseData(200, "成功导入并发布" + res + "条", res);
    }

    private String getVersionFromFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename().toUpperCase(Locale.ROOT);
        Assert.isTrue(originalFilename.startsWith("传输规范-"), "文件命名格式：传输规范-PP0XX.docx");
        String version = originalFilename.substring(originalFilename.indexOf("-") + 1, originalFilename.lastIndexOf("."));
        return version;
    }

    private void sendDingTalkMessage(NoticeForChange noticeForChange) {
        log.info(noticeForChange.getDingTalkNoticeMarkDownText());

        String webhookUrl = "https://oapi.dingtalk.com/robot/send?access_token=9b248bdea4db7b6b04dcbb0c99381400ffbde953f0879c35c00e28dbce97d147";
        DingTalkSender.sendDingAlarm(webhookUrl, "传输规范发布", noticeForChange.getDingTalkNoticeMarkDownText());
    }
}