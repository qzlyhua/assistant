package cn.qzlyhua.assistant.controller.api.csr;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.qzlyhua.assistant.controller.api.response.Response;
import cn.qzlyhua.assistant.dto.csr.GroupByBusinessArea;
import cn.qzlyhua.assistant.dto.csr.GroupByVersion;
import cn.qzlyhua.assistant.entity.ApiCsr;
import cn.qzlyhua.assistant.entity.ApiCsrParam;
import cn.qzlyhua.assistant.mapper.ApiCsrMapper;
import cn.qzlyhua.assistant.mapper.ApiCsrParamMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.ibatis.annotations.Delete;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.List;

/**
 * 传输规范
 *
 * @author yanghua
 */
@RestController
@Response
@RequestMapping("/api")
public class CsrController {
    @Resource
    ApiCsrMapper apiCsrMapper;

    @Resource
    ApiCsrParamMapper apiCsrParamMapper;

    @RequestMapping("/csrs/v")
    public List<GroupByVersion> getApisStatisticsByVersions() {
        return apiCsrMapper.statisticsByVersion();
    }

    @RequestMapping("/csrs/b")
    public List<GroupByBusinessArea> getApisStatisticsByBusinessArea() {
        return apiCsrMapper.statisticsByBusinessArea();
    }

    @RequestMapping("/csrs/version/{v}")
    public List<ApiCsr> getApisGroupByVersion(@PathVariable String v) {
        return apiCsrMapper.selectByVersion(v);
    }

    @RequestMapping("/csrs/business/{b}")
    public List<ApiCsr> getApisGroupByBusinessArea(@PathVariable String b) {
        return apiCsrMapper.selectByBusinessArea(b);
    }

    @RequestMapping("/csrs/all")
    public List<ApiCsr> getAllApis() {
        return apiCsrMapper.selectAll();
    }

    @RequestMapping("/csr/{id}")
    public Csr getApiById(@PathVariable String id) {
        ApiCsr apiCsr = apiCsrMapper.selectByPrimaryKey(Integer.valueOf(id));
        Assert.notNull(apiCsr, "传输规范不存在！");
        apiCsr.setReqParamsExample(prettyJson(apiCsr.getReqParamsExample()));
        apiCsr.setResParamsExample(prettyJson(apiCsr.getResParamsExample()));
        List<ApiCsrParam> req = apiCsrParamMapper.selectByCsrIdAndParameterType(apiCsr.getId(), "req");
        List<ApiCsrParam> res = apiCsrParamMapper.selectByCsrIdAndParameterType(apiCsr.getId(), "res");
        return new Csr(apiCsr, req, res);
    }

    @DeleteMapping("/csr/del/{id}")
    public void deleteApiById(@PathVariable String id) {
        ApiCsr apiCsr = apiCsrMapper.selectByPrimaryKey(Integer.valueOf(id));
        Assert.notNull(apiCsr, "传输规范不存在！");
        apiCsrMapper.deleteByPrimaryKey(Integer.valueOf(id));
        apiCsrParamMapper.deleteByCsrId(Integer.valueOf(id));
    }

    @Data
    @AllArgsConstructor
    private class Csr {
        private ApiCsr apiCsr;
        private List<ApiCsrParam> req;
        private List<ApiCsrParam> res;
    }

    /**
     * JSON字符串格式化：默认使用Jackson方案，若json格式存在问题，则不校验，使用HuTool格式化。
     *
     * @param json
     * @return
     */
    private String prettyJson(String json) {
        if (StrUtil.isBlank(json)) {
            json = "{\"_field_demo\":\"_value_demo\"}";
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
}
