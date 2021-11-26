package cn.qzlyhua.assistant.controller.api.csr;

import cn.hutool.core.collection.CollUtil;
import cn.qzlyhua.assistant.controller.api.response.Response;
import cn.qzlyhua.assistant.controller.api.response.ResponseData;
import cn.qzlyhua.assistant.dto.csr.message.InterfaceChangeInfo;
import cn.qzlyhua.assistant.dto.csr.message.NoticeForChange;
import cn.qzlyhua.assistant.dto.csr.message.Parameter;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        NoticeForChange noticeForChange = getNoticeForChange(version, origApiCsrs, origApiCsrParams);

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


    /**
     * 比较新版本传输规范与历史版本传输规范的差异，并输出NoticeForChange
     *
     * @param version
     * @param origApiCsrs
     * @param origApiCsrParams
     * @return
     */
    public NoticeForChange getNoticeForChange(String version, List<ApiCsr> origApiCsrs, List<ApiCsrParam> origApiCsrParams) {
        List<ApiCsr> apiCsrs = specificationService.getApiCsrsByVersion(version);
        List<ApiCsrParam> apiCsrParams = specificationService.getApiCsrParamsByVersion(version);

        NoticeForChange noticeForChange = new NoticeForChange();
        noticeForChange.setChangeTime(new Date());
        noticeForChange.setVersion(version);

        if (CollUtil.isEmpty(origApiCsrs)) {
            // 发布新版本
            log.info("传输规范-" + version + " 新版本发布！");
            noticeForChange.setNewVersion(true);
            return noticeForChange;
        } else {
            log.info("传输规范-" + version + " 更新！");
            noticeForChange.setNewVersion(false);

            // 传输规范Id与方法路径索引
            Map<Integer, String> origApiCsrsPathMap = new HashMap<>(origApiCsrs.size());
            Map<Integer, String> apiCsrsPathMap = new HashMap<>(apiCsrs.size());
            // 传输规范集合
            Map<String, ApiCsr> origApiCsrsMap = new HashMap<>(origApiCsrs.size());
            Map<String, ApiCsr> apiCsrsMap = new HashMap<>(apiCsrs.size());
            // 出入参集合
            Map<String, List<ApiCsrParam>> origApiCsrParamsMap = new HashMap<>(origApiCsrs.size() * 2);
            Map<String, List<ApiCsrParam>> apiCsrParamsMap = new HashMap<>(apiCsrs.size() * 2);

            // 比较对象（历史版本）数据整理
            for (ApiCsr oa : origApiCsrs) {
                origApiCsrParamsMap.put("req" + oa.getPath(), new ArrayList<>());
                origApiCsrParamsMap.put("res" + oa.getPath(), new ArrayList<>());
                origApiCsrsPathMap.put(oa.getId(), oa.getPath());
                origApiCsrsMap.put(oa.getPath(), oa);
            }

            for (ApiCsrParam op : origApiCsrParams) {
                String key = op.getParameterType() + origApiCsrsPathMap.get(op.getCsrId());
                origApiCsrParamsMap.get(key).add(op);
            }

            // 当前版本数据整理
            for (ApiCsr a : apiCsrs) {
                apiCsrParamsMap.put("req" + a.getPath(), new ArrayList<>());
                apiCsrParamsMap.put("res" + a.getPath(), new ArrayList<>());
                apiCsrsPathMap.put(a.getId(), a.getPath());
                apiCsrsMap.put(a.getPath(), a);
            }

            for (ApiCsrParam p : apiCsrParams) {
                String key = p.getParameterType() + apiCsrsPathMap.get(p.getCsrId());
                apiCsrParamsMap.get(key).add(p);
            }

            // 循环比较-删除接口
            for (ApiCsr oa : origApiCsrs) {
                if (!apiCsrsMap.containsKey(oa.getPath())) {
                    InterfaceChangeInfo delete = new InterfaceChangeInfo();
                    delete.setServicePath(oa.getPath());
                    delete.setServiceName(oa.getName());
                    noticeForChange.getInterfaceDeleted().add(delete);
                }
            }

            // 循环比较
            for (ApiCsr a : apiCsrs) {
                // 新增接口
                if (!origApiCsrsMap.containsKey(a.getPath())) {
                    InterfaceChangeInfo ad = new InterfaceChangeInfo();
                    ad.setServicePath(a.getPath());
                    ad.setServiceName(a.getName());
                    noticeForChange.getInterfaceAdded().add(ad);
                } else {
                    boolean anyChange = false;
                    InterfaceChangeInfo edit = new InterfaceChangeInfo();
                    edit.setServicePath(a.getPath());
                    edit.setServiceName(a.getName());

                    // 判断出入参是否有修改
                    List<ApiCsrParam> oriReqParamsList = origApiCsrParamsMap.get("req" + a.getPath());
                    List<ApiCsrParam> oriResParamsList = origApiCsrParamsMap.get("res" + a.getPath());
                    List<ApiCsrParam> reqParamsList = apiCsrParamsMap.get("req" + a.getPath());
                    List<ApiCsrParam> resParamsList = apiCsrParamsMap.get("res" + a.getPath());

                    // 数据整理
                    Map<String, ApiCsrParam> oriReqParamsMap = oriReqParamsList.stream().collect(Collectors.toMap(ApiCsrParam::getKey, Function.identity(), (key1, key2) -> key2));
                    Map<String, ApiCsrParam> oriResParamsMap = oriResParamsList.stream().collect(Collectors.toMap(ApiCsrParam::getKey, Function.identity(), (key1, key2) -> key2));
                    Map<String, ApiCsrParam> reqParamsMap = reqParamsList.stream().collect(Collectors.toMap(ApiCsrParam::getKey, Function.identity(), (key1, key2) -> key2));
                    Map<String, ApiCsrParam> resParamsMap = resParamsList.stream().collect(Collectors.toMap(ApiCsrParam::getKey, Function.identity(), (key1, key2) -> key2));

                    // 入参检查-删除（在新入参集合内，找不到历史入参）
                    for (ApiCsrParam o : oriReqParamsList) {
                        if (!reqParamsMap.containsKey(o.getKey())) {
                            anyChange = true;
                            edit.getReqParamsDeleted().add(Parameter.builder().key(o.getKey()).type(o.getType())
                                    .des(o.getDescribe()).isRequired(o.getRequired()).build());
                        }
                    }

                    // 入参检查-新增（在历史入参集合内，找不到新入参）
                    for (ApiCsrParam o : reqParamsList) {
                        if (!oriReqParamsMap.containsKey(o.getKey())) {
                            anyChange = true;
                            edit.getReqParamsAdded().add(Parameter.builder().key(o.getKey()).type(o.getType())
                                    .des(o.getDescribe()).isRequired(o.getRequired()).build());
                        }
                    }

                    // 入参检查-修改
                    for (ApiCsrParam q : reqParamsList) {
                        if (oriReqParamsMap.containsKey(q.getKey())) {
                            ApiCsrParam o = oriReqParamsMap.get(q.getKey());
                            if (!o.getType().equals(q.getType())
                                    || !o.getDescribe().equals(q.getDescribe())
                                    || !o.getRequired().equals(q.getRequired())) {
                                anyChange = true;
                                edit.getReqParamsEdited().add(Parameter.builder().key(q.getKey())
                                        .type(o.getType().equals(q.getType()) ? q.getType() : o.getType() + " > " + q.getType())
                                        .des(o.getDescribe().equals(q.getDescribe()) ? q.getDescribe() : o.getDescribe() + " > " + q.getDescribe())
                                        .isRequired(o.getRequired().equals(q.getRequired()) ? q.getRequired() : o.getRequired() + " > " + q.getRequired())
                                        .build());
                            }

                        }
                    }

                    // 出参检查-删除（在新出参集合内，找不到历史出参）
                    for (ApiCsrParam o : oriResParamsList) {
                        if (!resParamsMap.containsKey(o.getKey())) {
                            anyChange = true;
                            edit.getResParamsDeleted().add(Parameter.builder().key(o.getKey()).type(o.getType())
                                    .des(o.getDescribe()).isRequired(o.getRequired()).build());
                        }
                    }

                    // 出参检查-新增（在历史出参集合内，找不到新出参）
                    for (ApiCsrParam o : resParamsList) {
                        if (!oriResParamsMap.containsKey(o.getKey())) {
                            anyChange = true;
                            edit.getResParamsAdded().add(Parameter.builder().key(o.getKey()).type(o.getType())
                                    .des(o.getDescribe()).isRequired(o.getRequired()).build());
                        }
                    }

                    // 出参检查-修改
                    for (ApiCsrParam s : resParamsList) {
                        if (oriResParamsMap.containsKey(s.getKey())) {
                            ApiCsrParam o = oriResParamsMap.get(s.getKey());
                            if (!o.getType().equals(s.getType())
                                    || !o.getDescribe().equals(s.getDescribe())
                                    || !o.getRequired().equals(s.getRequired())) {
                                anyChange = true;
                                edit.getResParamsEdited().add(Parameter.builder().key(s.getKey())
                                        .type(o.getType().equals(s.getType()) ? s.getType() : o.getType() + " > " + s.getType())
                                        .des(o.getDescribe().equals(s.getDescribe()) ? s.getDescribe() : o.getDescribe() + " > " + s.getDescribe())
                                        .isRequired(o.getRequired().equals(s.getRequired()) ? s.getRequired() : o.getRequired() + " > " + s.getRequired())
                                        .build());
                            }
                        }
                    }

                    // 任何参数存在修改，则添加到InterfaceEdited
                    if (anyChange) {
                        noticeForChange.getInterfaceEdited().add(edit);
                    }
                }
            }
            return noticeForChange;
        }
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