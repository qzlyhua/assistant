package cn.qzlyhua.assistant.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.qzlyhua.assistant.dto.specification.Chapter;
import cn.qzlyhua.assistant.dto.specification.Parameter;
import cn.qzlyhua.assistant.dto.specification.Service;
import cn.qzlyhua.assistant.entity.ApiCsr;
import cn.qzlyhua.assistant.entity.ApiCsrParam;
import cn.qzlyhua.assistant.mapper.ApiCsrMapper;
import cn.qzlyhua.assistant.mapper.ApiCsrParamMapper;
import cn.qzlyhua.assistant.service.SpecificationService;
import cn.qzlyhua.assistant.util.WordUtil;
import cn.qzlyhua.assistant.util.word.TransmissionSpecification;
import cn.qzlyhua.assistant.util.word.TransmissionSpecificationParam;
import com.deepoove.poi.plugin.highlight.HighlightRenderData;
import com.deepoove.poi.plugin.highlight.HighlightStyle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yanghua
 */
@org.springframework.stereotype.Service
@Slf4j
public class SpecificationServiceImpl implements SpecificationService {
    @Resource
    ApiCsrMapper apiCsrMapper;

    @Resource
    ApiCsrParamMapper apiCsrParamMapper;

    @Override
    public List<Chapter> getSpecificationsByVersion(String version) {
        List<ApiCsr> apiCsrs = apiCsrMapper.selectByVersion(version);
        return getSpecifications(apiCsrs);
    }

    @Override
    public List<Chapter> getSpecificationsByBusinessArea(String areaName) {
        List<ApiCsr> apiCsrs = apiCsrMapper.selectByBusinessArea(areaName);
        return getSpecifications(apiCsrs);
    }

    /**
     * 将数据库存储数据对象转换为word模板所需数据对象
     *
     * @param apiCsrs
     * @return
     */
    private List<Chapter> getSpecifications(List<ApiCsr> apiCsrs) {
        if (CollUtil.isEmpty(apiCsrs)) {
            return new ArrayList<>();
        }

        Set<String> businessAreas = apiCsrs.stream().map(ApiCsr::getBusinessArea).collect(Collectors.toSet());

        Map<String, List<ApiCsr>> map = new HashMap<>(businessAreas.size());
        for (String bizAreaName : businessAreas) {
            map.put(bizAreaName, new ArrayList<>());
        }

        for (ApiCsr apiCsr : apiCsrs) {
            map.get(apiCsr.getBusinessArea()).add(apiCsr);
        }

        List<Chapter> result = new ArrayList<>();
        for (String bizAreaName : businessAreas) {
            List<Service> services = new ArrayList<>();
            List<ApiCsr> apis = map.get(bizAreaName);
            for (ApiCsr a : apis) {
                List<Parameter> reqParameters = new ArrayList<>();
                List<ApiCsrParam> reqCsrParams = apiCsrParamMapper.selectByCsrIdAndParameterType(a.getId(), "req");
                for (ApiCsrParam q : reqCsrParams) {
                    reqParameters.add(Parameter.builder()
                            .key(q.getKey())
                            .des(q.getDescribe())
                            .type(q.getType())
                            .isRequired(q.getRequired()).build());
                }

                List<Parameter> resParameters = new ArrayList<>();
                List<ApiCsrParam> resCsrParams = apiCsrParamMapper.selectByCsrIdAndParameterType(a.getId(), "res");
                for (ApiCsrParam s : resCsrParams) {
                    resParameters.add(Parameter.builder()
                            .key(s.getKey())
                            .des(s.getDescribe())
                            .type(s.getType())
                            .isRequired(s.getRequired()).build());
                }

                Service service = Service.builder()
                        .serviceName(a.getPath())
                        .serviceNick(a.getName())
                        .description(a.getDescription())
                        .explain(a.getRemarks())
                        .reqParameters(reqParameters)
                        .reqExample(getHighlightRenderData(a.getReqParamsExample()))
                        .resParameters(resParameters)
                        .resExample(getHighlightRenderData(a.getResParamsExample())).build();
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
     * JSON代码块（带高亮效果）
     *
     * @param code
     * @return
     */
    private HighlightRenderData getHighlightRenderData(String code) {
        HighlightRenderData source = new HighlightRenderData();
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
    public void importSpecificationsFromWord(MultipartFile file, String version) throws IOException {
        List<TransmissionSpecification> list = WordUtil.getAnalysisResult(file, version);
        importSpecificationsFromWordToDb(list);
    }

    /**
     * word文件导入
     * 本地文件模式
     */
    @Override
    public void importSpecificationsFromWord(File file, String version) throws IOException {
        List<TransmissionSpecification> list = WordUtil.getAnalysisResult(file, version);
        importSpecificationsFromWordToDb(list);
    }

    /**
     * word文件导入-入库处理
     */
    public void importSpecificationsFromWordToDb(List<TransmissionSpecification> list) throws IOException {
        for (TransmissionSpecification e : list) {
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
    }
}
