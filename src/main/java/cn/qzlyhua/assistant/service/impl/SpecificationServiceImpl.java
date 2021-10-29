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

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public List<Chapter> getSpecifications() {
        Chapter c1 = Chapter.builder()
                .headWord("家庭档案")
                .services(new ArrayList<Service>() {{
                    add(renderService());
                    add(renderService());
                }}).build();

        Chapter c2 = Chapter.builder()
                .headWord("个人档案")
                .services(new ArrayList<Service>() {{
                    add(renderService());
                    add(renderService());
                }}).build();

        Chapter c3 = Chapter.builder()
                .headWord("业务领域")
                .services(new ArrayList<Service>() {{
                    add(renderService());
                    add(renderService());
                }}).build();

        return new ArrayList<Chapter>() {{
            add(c1);
            add(c2);
            add(c3);
        }};
    }


    /**
     * TODO 获取服务传输规范文档
     *
     * @return
     */
    private Service renderService() {
        String requestJson = "{\"IDNO\":\"330624199001010123\",\"IDType\":\"1\"}";
        String resultJson = "{\"code\":200,\"message\":\"Maskit general success\",\"result\":{\"size\":1,\"data\":{\"testCategName\":\"\",\"equipmentCode\":\"\",\"testPatResourceName\":\"体检\",\"patName\":\"舒丽英\",\"inhospIndexNo\":\"6211005177\",\"executDrIndexNo\":\"\",\"maritalStatusCode\":\"\",\"testPatResourceCode\":\"5\",\"reportDate\":\"2021-10-25 19:14:30\",\"equipmentName\":\"\",\"reportNo\":\"20211025G0225036\",\"executDrName\":\"\",\"orderNo\":\"\",\"orgName\":\"新昌县中医院\",\"proofDate\":\"2021-10-25 19:14:30\",\"applyDeptIndexNo\":\"\",\"executDate\":\"2021-10-25 16:48:12\",\"executDeptCode\":\"\",\"microbeTestFlag\":\"\",\"inhospNum\":\"\",\"applyDrIndexNo\":\"\",\"patIndexNo\":\"20211025G0225036\",\"maritalStatusName\":\"\",\"applyDrName\":\"xzh\",\"testCategCode\":\"\",\"dateBirth\":\"\",\"reportDrName\":\"陈伟军\",\"anamnesisNo\":\"6211005177\",\"miCode\":\"33D003\",\"note\":\"镜岭卫生院2\",\"updateDate\":\"\",\"proofDrCode\":\"\",\"reportDrCode\":\"\",\"executDeptIndexNo\":\"\",\"reportName\":\"新型冠状病毒RNA检测\",\"clinicDiagName\":\"\",\"sampleNo\":\"20211025G0225036\",\"executDrCode\":\"\",\"idNumber\":\"330624197701272044\",\"executDeptName\":\"检验科\",\"ethnicName\":\"\",\"applyDrCode\":\"\",\"clinicDiagCode\":\"\",\"sampleTypeName\":\"咽拭子\",\"physiSexName\":\"女\",\"orgCode\":\"\",\"recordDate\":\"\",\"proofDrName\":\"\",\"testClassCode\":\"\",\"reportDrIndexNo\":\"\",\"outhospNo\":\"\",\"proofFlag\":\"\",\"testClassName\":\"\",\"hospCode\":\"0201001\",\"sampleTypeCode\":\"LIS1045\",\"applyDeptCode\":\"\",\"electrRequisitionNo\":\"0458044600\",\"testReportUrl\":\"\",\"applyDeptName\":\"\",\"mrNo\":\"6211005177\",\"physiSexCode\":\"2\",\"orderGroupNo\":\"\",\"inhospNo\":\"\",\"outhospIndexNo\":\"6211005177\",\"visitCartNo\":\"6211005177\",\"applyDate\":\"2021-10-22 14:54:28\",\"ethnicCode\":\"\"},\"list\":[{\"note\":\"镜岭卫生院2\",\"miCode\":\"33D003\",\"updateDate\":\"\",\"testResultValue\":\"阴性\",\"equipmentCode\":\"\",\"mic\":\"\",\"microbeName\":\"\",\"testResultFlag\":\"\",\"sampleTypeName\":\"咽拭子\",\"diameter\":\"\",\"antibioticsName\":\"\",\"orgCode\":\"\",\"testResultValueUnit\":\"Copies/ml\",\"recordDate\":\"2021-10-25 16:48:13\",\"referenceRanges\":\"阴性\",\"equipmentName\":\"\",\"reportNo\":\"20211025G0225036\",\"orgName\":\"新昌县中医院\",\"hospCode\":\"0201001\",\"sampleTypeCode\":\"LIS1045\",\"testItemCode\":\"5750\",\"bacterialColonyCount\":\"\",\"invalidFlag\":\"\",\"testItemName\":\"新型冠状病毒核酸检测\",\"electrRequisitionNo\":\"0458044600\",\"smearResult\":\"\"}]}}";


        Parameter parameter1 = Parameter.builder()
                .key("IDType")
                .des("证件类别")
                .type("字符串")
                .isRequired("Y").build();

        Parameter parameter2 = Parameter.builder()
                .key("IDNO")
                .des("证件号码")
                .type("字符串")
                .isRequired("Y").build();

        Parameter parameter3 = Parameter.builder()
                .key("id")
                .des("记录主键")
                .type("字符串")
                .isRequired("Y").build();

        Service service = Service.builder()
                .serviceName("getAbcByDefghijklMnopqrst")
                .serviceNick("示例方法")
                .description("根据DEF获取ABC根据DEF获取ABC根据DEF获取ABC根据DEABC根据DEF获取ABC根据DEF获取ABC")
                .explain("该接口为示例接口，无实际作用无实际作用无实际作用无实无实际作用无实际作用无实际作作用无实际作用无实际作用无实际作用")
                .reqParameters(new ArrayList<Parameter>() {{
                    add(parameter1);
                    add(parameter2);
                }})
//                .reqParameters(new ArrayList<>())
                .reqExample(getHighlightRenderData(requestJson))
                .resParameters(new ArrayList<Parameter>() {{
                    add(parameter3);
                }})
//                .resParameters(new ArrayList<>())
                .resExample(getHighlightRenderData(resultJson)).build();
        return service;
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
            e.printStackTrace();
            // HuTool 方案。不校验JSON格式。
            return JSONUtil.formatJsonStr(json);
        }
    }

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
     * word文件导入
     *
     * @param word
     * @param version
     */
    @Override
    public void importSpecificationsFromWord(File word, String version) {
        String wordPath = "/Users/yanghua/Downloads/传输规范-PP012.doc";

        List<TransmissionSpecification> list = WordUtil.getAnalysisResult(wordPath, "PP012");

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
