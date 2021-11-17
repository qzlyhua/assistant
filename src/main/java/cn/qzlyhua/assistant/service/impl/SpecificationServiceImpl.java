package cn.qzlyhua.assistant.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileAppender;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.qzlyhua.assistant.dto.specification.Chapter;
import cn.qzlyhua.assistant.dto.specification.DictionaryTable;
import cn.qzlyhua.assistant.dto.specification.Parameter;
import cn.qzlyhua.assistant.dto.specification.Service;
import cn.qzlyhua.assistant.entity.ApiCsr;
import cn.qzlyhua.assistant.entity.ApiCsrDic;
import cn.qzlyhua.assistant.entity.ApiCsrParam;
import cn.qzlyhua.assistant.mapper.ApiCsrDicMapper;
import cn.qzlyhua.assistant.mapper.ApiCsrMapper;
import cn.qzlyhua.assistant.mapper.ApiCsrParamMapper;
import cn.qzlyhua.assistant.service.SpecificationService;
import cn.qzlyhua.assistant.util.word.*;
import com.deepoove.poi.plugin.highlight.HighlightRenderData;
import com.deepoove.poi.plugin.highlight.HighlightStyle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @author yanghua
 */
@org.springframework.stereotype.Service
@Slf4j
public class SpecificationServiceImpl implements SpecificationService {
    private static final String GROUP_TYPE_BUSINESS = "business";
    private static final String GROUP_TYPE_VERSION = "version";

    @Resource
    ApiCsrMapper apiCsrMapper;

    @Resource
    ApiCsrParamMapper apiCsrParamMapper;

    @Resource
    ApiCsrDicMapper apiCsrDicMapper;

    @Override
    public List<Chapter> getSpecificationsByVersion(String version) {
        List<ApiCsr> apiCsrs = apiCsrMapper.selectByVersion(version);
        return getSpecifications(apiCsrs, GROUP_TYPE_VERSION);
    }

    @Override
    public List<Chapter> getSpecificationsByBusinessArea(String areaName) {
        List<ApiCsr> apiCsrs = apiCsrMapper.selectByBusinessArea(areaName);
        return getSpecifications(apiCsrs, GROUP_TYPE_BUSINESS);
    }

    @Override
    public List<String> getSpecificationsBusinessAreaByUpdateTime(Date time) {
        return apiCsrMapper.selectBusinessAreaByUpdateTimeAfter(time);
    }

    @Override
    public List<String> getSpecificationsBusinessAreaByVersion(String version) {
        return apiCsrMapper.selectBusinessAreaByVersion(version);
    }

    /**
     * 根据文档类型，提取业务领域
     *
     * @param apiCsr
     * @param type
     * @return
     */
    private String getFullBusAreaName(ApiCsr apiCsr, String type) {
        // 按业务领域取时，使用二级业务领域名称作为分类依据
        if (GROUP_TYPE_BUSINESS.equals(type)) {
            return apiCsr.getBusinessSubArea();
        } else {
            return apiCsr.getBusinessArea().equals(apiCsr.getBusinessSubArea()) ?
                    apiCsr.getBusinessArea() :
                    (apiCsr.getBusinessArea() + "（" + apiCsr.getBusinessSubArea() + "）");
        }
    }

    /**
     * 将数据库存储数据对象转换为word模板所需数据对象
     *
     * @param apiCsrs
     * @return
     */
    private List<Chapter> getSpecifications(List<ApiCsr> apiCsrs, String type) {
        if (CollUtil.isEmpty(apiCsrs)) {
            return new ArrayList<>();
        }

        Set<String> businessAreas = new LinkedHashSet<>();

        // 记录所有ID，用于一次查询所有出入参
        List<Integer> ids = new ArrayList<>(apiCsrs.size());
        for (ApiCsr apiCsr : apiCsrs) {
            // 记录所有业务领域名称
            String fullBusAreaName = getFullBusAreaName(apiCsr, type);
            businessAreas.add(fullBusAreaName);

            ids.add(apiCsr.getId());
        }

        Map<String, List<ApiCsrParam>> paramsMap = new HashMap<>();
        // 查询所有涉及的出入参
        List<ApiCsrParam> params = apiCsrParamMapper.selectByCsrIdIn(ids);
        for (ApiCsrParam p : params) {
            // 以传输规范ID+出入参类型为key，进行整理
            String key = p.getCsrId() + p.getType();
            if (paramsMap.containsKey(key)) {
                paramsMap.get(key).add(p);
            } else {
                paramsMap.put(key, new ArrayList() {{add(p);}});
            }
        }

        Map<String, List<ApiCsr>> map = new HashMap<>(businessAreas.size());
        for (String bizAreaName : businessAreas) {
            map.put(bizAreaName, new ArrayList<>());
        }

        for (ApiCsr apiCsr : apiCsrs) {
            String fullBusAreaName = getFullBusAreaName(apiCsr, type);
            map.get(fullBusAreaName).add(apiCsr);
        }

        List<Chapter> result = new ArrayList<>();
        for (String bizAreaName : businessAreas) {
            List<Service> services = new ArrayList<>();
            List<ApiCsr> apis = map.get(bizAreaName);
            for (ApiCsr a : apis) {
                Set<String> dicTypes = new HashSet<>();

                List<Parameter> reqParameters = new ArrayList<>();
                // List<ApiCsrParam> reqCsrParams = apiCsrParamMapper.selectByCsrIdAndParameterType(a.getId(), "req");
                List<ApiCsrParam> reqCsrParams = paramsMap.get(a.getId() + "req");
                if (CollUtil.isNotEmpty(reqCsrParams)) {
                    for (ApiCsrParam q : reqCsrParams) {
                        getDicTypeName(q.getDescribe(), dicTypes);

                        reqParameters.add(Parameter.builder()
                                .key(q.getKey())
                                .des(q.getDescribe())
                                .type(q.getType())
                                .isRequired(q.getRequired()).build());
                    }
                }

                List<Parameter> resParameters = new ArrayList<>();
                // List<ApiCsrParam> resCsrParams = apiCsrParamMapper.selectByCsrIdAndParameterType(a.getId(), "res");
                List<ApiCsrParam> resCsrParams = paramsMap.get(a.getId() + "res");
                if (CollUtil.isNotEmpty(resCsrParams)) {
                    for (ApiCsrParam s : resCsrParams) {
                        getDicTypeName(s.getDescribe(), dicTypes);

                        resParameters.add(Parameter.builder()
                                .key(s.getKey())
                                .des(s.getDescribe())
                                .type(s.getType())
                                .isRequired(s.getRequired()).build());
                    }
                }

                List<DictionaryTable> dictionaryTableList = new ArrayList<>();
                if (!dicTypes.isEmpty()) {
                    for (String t : dicTypes) {
                        List<ApiCsrDic> list = apiCsrDicMapper.selectAllByType(t);
                        List<cn.qzlyhua.assistant.dto.specification.Dictionary> dictionaries = new ArrayList<>();
                        for (ApiCsrDic d : list) {
                            dictionaries.add(cn.qzlyhua.assistant.dto.specification.Dictionary.builder()
                                    .code(d.getCode())
                                    .name(d.getName())
                                    .build());
                        }

                        if (CollUtil.isNotEmpty(list)) {
                            dictionaryTableList.add(DictionaryTable.builder()
                                    .type(t)
                                    .size(list.size())
                                    .dictionaryList(dictionaries)
                                    .build());
                        }
                    }
                }

                Service service = Service.builder()
                        .serviceName(a.getPath())
                        .serviceNick(a.getName())
                        .description(a.getDescription())
                        .explain(a.getRemarks() == null ? "无" : a.getRemarks())
                        .reqParameters(reqParameters)
                        .reqExampleStr(prettyJson(a.getReqParamsExample()))
                        .reqExample(getHighlightRenderData(a.getReqParamsExample()))
                        .resParameters(resParameters)
                        .resExampleStr(prettyJson(a.getResParamsExample()))
                        .resExample(getHighlightRenderData(a.getResParamsExample()))
                        .dictionaries(dictionaryTableList).build();
                services.add(service);
            }

            Chapter chapter = Chapter.builder()
                    .headWord(bizAreaName)
                    .services(services).build();
            result.add(chapter);
        }

        return result;
    }

    /**
     * 从出入参描述中提取字典类型信息
     *
     * @param des
     * @param set
     */
    private void getDicTypeName(String des, Set<String> set) {
        String key = "字典";
        String endKey = "）";
        if (des.contains(key)) {
            des = des.replaceAll("\\)", "）")
                    .replaceAll(":", "：")
                    .replaceAll("字典数据", "数据字典");
            String idx = key + "：";
            if (des.contains(idx) && des.contains(endKey)) {
                set.add(des.substring(des.indexOf(idx) + idx.length(), des.indexOf(endKey)));
            }
        }
    }

    /**
     * Word文件内插入代码块（带高亮效果）
     *
     * @param code
     * @return
     */
    private HighlightRenderData getHighlightRenderData(String code) {
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

    /**
     * JSON字符串格式化：默认使用Jackson方案，若json格式存在问题，则不校验，使用HuTool格式化。
     *
     * @param json
     * @return
     */
    private String prettyJson(String json) {
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

    /**
     * word文件导入
     * 网络上传文件模式
     */
    @Override
    public int importSpecificationsFromWord(MultipartFile file, String version) throws IOException {
        CsrBook book = file.getOriginalFilename().endsWith("docx") ?
                DocxUtil.getAnalysisResult(file, version) :
                DocUtil.getAnalysisResult(file, version);
        return importSpecificationsFromWordToDb(book.getTransmissionSpecifications(), book.getDictionaries());
    }

    @Override
    public void publishMarkDownFilesByVersion(List<Chapter> chapters, String version) {
        String mdFilePath = "/soft/frontend/docs/" + version + ".md";
        if (FileUtil.exist(mdFilePath)) {
            FileUtil.del(mdFilePath);
        }
        FileUtil.touch(mdFilePath);
        FileAppender appender = new FileAppender(FileUtil.file(mdFilePath), 16, true);
        appender.append("# " + version);
        appender.append("> 最近更新：" + DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
        appender.append("----\n");
        for (Chapter c : chapters) {
            appendFileByChapter(appender, c);
        }
        appender.append("© 2021 新昌医惠数字科技有限公司. All Rights Reserved.");

        appender.flush();
        appender.toString();

        String sideBarMdFile = "/soft/frontend/docs/_sidebar.md";
        String sideBarStr = FileUtil.readUtf8String(sideBarMdFile);
        String currentSide = "* [" + version + "](/" + version + ".md)";
        if (!sideBarStr.contains(currentSide)) {
            sideBarStr = sideBarStr.replace("* [概述](/)", "* [概述](/)\n" + currentSide);
        }
        FileUtil.del(sideBarMdFile);
        FileUtil.touch(sideBarMdFile);
        FileUtil.writeUtf8String(sideBarStr, sideBarMdFile);
    }

    @Override
    public void publishMarkDownFilesByBusinessArea(List<Chapter> chapters, String businessArea) {
        String mdFilePath = "/soft/frontend/docs/business/" + businessArea + ".md";
        if (FileUtil.exist(mdFilePath)) {
            FileUtil.del(mdFilePath);
        }
        FileUtil.touch(mdFilePath);
        FileAppender appender = new FileAppender(FileUtil.file(mdFilePath), 16, true);
        appender.append("# " + businessArea);
        appender.append("> 最近更新：" + DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
        appender.append("----\n");
        for (Chapter c : chapters) {
            appendFileByChapter(appender, c);
        }
        appender.append("© 2021 新昌医惠数字科技有限公司. All Rights Reserved.");

        appender.flush();
        appender.toString();

        String sideBarMdFile = "/soft/frontend/docs/business/_sidebar.md";
        String sideBarStr = FileUtil.readUtf8String(sideBarMdFile);
        String currentSide = "* [" + businessArea + "](/business/" + businessArea + ".md)";
        if (!sideBarStr.contains(currentSide)) {
            sideBarStr = sideBarStr.replace("* [概述](/business/)", "* [概述](/business/)\n" + currentSide);
        }
        FileUtil.del(sideBarMdFile);
        FileUtil.touch(sideBarMdFile);
        FileUtil.writeUtf8String(sideBarStr, sideBarMdFile);
    }

    /**
     * word文件导入-入库处理
     */
    public int importSpecificationsFromWordToDb(List<TransmissionSpecification> transmissionSpecifications, List<cn.qzlyhua.assistant.util.word.Dictionary> dictionaries) {
        for (TransmissionSpecification e : transmissionSpecifications) {
            String path = e.getPath();
            ApiCsr tmp = apiCsrMapper.selectOneByPath(path);
            if (tmp != null) {
                apiCsrMapper.deleteByPrimaryKey(tmp.getId());
                apiCsrParamMapper.deleteByCsrId(tmp.getId());
            }

            ApiCsr apiCsr = new ApiCsr();
            apiCsr.setPath(e.getPath());
            apiCsr.setName(e.getName());
            apiCsr.setDescription(e.getDescription());
            apiCsr.setRemarks(e.getRemarks());
            apiCsr.setReqParamsExample(e.getReqParamsExample());
            apiCsr.setResParamsExample(e.getResParamsExample());
            apiCsr.setVersion(e.getVersion());
            apiCsr.setBusinessArea(e.getBusinessArea());
            apiCsr.setBusinessSubArea(e.getBusinessSubArea());
            apiCsr.setCreateTime(new Date());
            apiCsr.setUpdateTime(new Date());

            apiCsrMapper.insert(apiCsr);

            List<ApiCsrParam> params = new ArrayList<>();
            List<TransmissionSpecificationParam> reqParams = e.getReqParams();
            if (reqParams != null && CollUtil.isNotEmpty(reqParams)) {
                for (TransmissionSpecificationParam a : reqParams) {
                    ApiCsrParam apiCsrParam = new ApiCsrParam();
                    apiCsrParam.setCsrId(apiCsr.getId());
                    apiCsrParam.setParameterType("req");
                    apiCsrParam.setKey(a.getKey());
                    apiCsrParam.setType(a.getType());
                    apiCsrParam.setDescribe(a.getDescribe());
                    apiCsrParam.setRequired(a.getRequired());
                    params.add(apiCsrParam);
                }
            }

            List<TransmissionSpecificationParam> resParams = e.getResParams();
            if (resParams != null && CollUtil.isNotEmpty(resParams)) {
                for (TransmissionSpecificationParam a : resParams) {
                    ApiCsrParam apiCsrParam = new ApiCsrParam();
                    apiCsrParam.setCsrId(apiCsr.getId());
                    apiCsrParam.setParameterType("res");
                    apiCsrParam.setKey(a.getKey());
                    apiCsrParam.setType(a.getType());
                    apiCsrParam.setDescribe(a.getDescribe());
                    apiCsrParam.setRequired(a.getRequired());
                    params.add(apiCsrParam);
                }
            }

            if (CollUtil.isNotEmpty(params)) {
                apiCsrParamMapper.batchInsert(params);
            }
        }

        List<ApiCsrDic> csrDics = new ArrayList<>();
        for (cn.qzlyhua.assistant.util.word.Dictionary d : dictionaries) {
            ApiCsrDic apiCsrDic = new ApiCsrDic();
            apiCsrDic.setType(d.getType());
            apiCsrDic.setCode(d.getCode());
            apiCsrDic.setName(d.getName());
            csrDics.add(apiCsrDic);

            apiCsrDicMapper.deleteByTypeAndCode(d.getType(), d.getCode());
        }

        if (CollUtil.isNotEmpty(csrDics)) {
            apiCsrDicMapper.batchInsert(csrDics);
        }

        return transmissionSpecifications.size();
    }

    private void appendFileByChapter(FileAppender appender, Chapter chapter) {
        appender.append("## " + chapter.getHeadWord());
        for (Service s : chapter.getServices()) {
            appender.append("");
            appender.append("### `" + s.getServiceName() + "`（" + s.getServiceNick() + "）");
            appender.append("#### 功能描述：");
            appender.append(s.getDescription());
            if (!StrUtil.isBlank(s.getExplain()) && !"无".equals(s.getExplain())) {
                appender.append("");
                appender.append("!> " + s.getExplain());
                appender.append("");
            }

            if (CollUtil.isNotEmpty(s.getReqParameters())) {
                appender.append("#### 入参说明：");
                appender.append("| 属性名 | 类型 | 描述 | 必填 |");
                appender.append("| :----- | :----: | :----- | :----: |");
                for (Parameter p : s.getReqParameters()) {
                    String parameter = "| " + p.getKey()
                            + " | " + p.getType().replaceAll("\n", "<br/>")
                            + " | " + p.getDes().replaceAll("\n", "<br/>")
                            + " | " + p.getIsRequired().replaceAll("\n", "<br/>") + " |";
                    appender.append(parameter);
                }
            }

            if (CollUtil.isNotEmpty(s.getResParameters())) {
                appender.append("#### 出参说明：");
                appender.append("| 属性名 | 类型 | 描述 | 必填 |");
                appender.append("| :----- | :----: | :----- | :----: |");
                for (Parameter p : s.getResParameters()) {
                    String parameter = "| " + p.getKey()
                            + " | " + p.getType().replaceAll("\n", "<br/>")
                            + " | " + p.getDes().replaceAll("\n", "<br/>")
                            + " | " + p.getIsRequired().replaceAll("\n", "<br/>") + " |";
                    appender.append(parameter);
                }
            }

            if (CollUtil.isNotEmpty(s.getDictionaries())) {
                appender.append("#### 数据字典：");

                appender.append("<table>");
                appender.append("<tr><td>字典类别</td><td>代码</td><td>含义</td></tr>");
                for (DictionaryTable d : s.getDictionaries()) {
                    appender.append("<tr><td rowspan=\"" + d.getSize() + "\">" + d.getType() + "</td><td>" +
                            d.getDictionaryList().get(0).getCode() + "</td><td>" +
                            d.getDictionaryList().get(0).getName() + "</td></tr>");
                    for (int i = 1; i <= d.getDictionaryList().size() - 1; i++) {
                        cn.qzlyhua.assistant.dto.specification.Dictionary dictionary = d.getDictionaryList().get(i);
                        appender.append("<tr><td>" + dictionary.getCode() + "</td><td>" + dictionary.getName() + "</td></tr>");
                    }
                }
                appender.append("</table>");
            }
            appender.append("");

            if (!StrUtil.isBlank(s.getReqExampleStr())) {
                appender.append("#### 入参举例：");
                appender.append(s.getReqExampleStr().trim().startsWith("curl") ? "```bash" : "```json");
                appender.append(s.getReqExampleStr());
                appender.append("```");
            } else if (CollUtil.isNotEmpty(s.getReqParameters()) && StrUtil.isBlank(s.getReqExampleStr())) {
                appender.append("#### 入参举例：");
                appender.append("");
                appender.append("?> _TODO_ 待完善");
                appender.append("");
            }

            if (!StrUtil.isBlank(s.getResExampleStr())) {
                appender.append("#### 出参举例：");
                appender.append(s.getResExampleStr().trim().startsWith("curl") ? "```bash" : "```json");
                appender.append(s.getResExampleStr());
                appender.append("```");
            } else if (CollUtil.isNotEmpty(s.getResParameters()) && StrUtil.isBlank(s.getResExampleStr())) {
                appender.append("#### 出参举例：");
                appender.append("");
                appender.append("?> _TODO_ 待完善");
                appender.append("");
            }

            appender.append("----");
        }
    }
}
