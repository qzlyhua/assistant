package cn.qzlyhua.assistant.service.impl;

import cn.hutool.core.collection.CollUtil;
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

    /**
     * 根据文档类型，提取业务领域
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

        for (ApiCsr apiCsr : apiCsrs) {
            String fullBusAreaName = getFullBusAreaName(apiCsr, type);
            businessAreas.add(fullBusAreaName);
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
                List<ApiCsrParam> reqCsrParams = apiCsrParamMapper.selectByCsrIdAndParameterType(a.getId(), "req");
                for (ApiCsrParam q : reqCsrParams) {
                    getDicTypeName(q.getDescribe(), dicTypes);

                    reqParameters.add(Parameter.builder()
                            .key(q.getKey())
                            .des(q.getDescribe())
                            .type(q.getType())
                            .isRequired(q.getRequired()).build());
                }

                List<Parameter> resParameters = new ArrayList<>();
                List<ApiCsrParam> resCsrParams = apiCsrParamMapper.selectByCsrIdAndParameterType(a.getId(), "res");
                for (ApiCsrParam s : resCsrParams) {
                    getDicTypeName(s.getDescribe(), dicTypes);

                    resParameters.add(Parameter.builder()
                            .key(s.getKey())
                            .des(s.getDescribe())
                            .type(s.getType())
                            .isRequired(s.getRequired()).build());
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
     * JSON代码块（带高亮效果）
     *
     * @param code
     * @return
     */
    private HighlightRenderData getHighlightRenderData(String code) {
        HighlightRenderData source = new HighlightRenderData();
        // 不能返回空，会导致渲染时报错
        source.setCode(StrUtil.isNotBlank(code) ? prettyJson(code) : "{\"_key\":\"_value\"}");
        source.setLanguage("json");
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
}
