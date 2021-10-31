package cn.qzlyhua.assistant.controller.api;

import cn.qzlyhua.assistant.controller.api.response.Response;
import cn.qzlyhua.assistant.dto.csr.GroupByBusinessArea;
import cn.qzlyhua.assistant.dto.csr.GroupByVersion;
import cn.qzlyhua.assistant.entity.ApiCsr;
import cn.qzlyhua.assistant.mapper.ApiCsrMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户域概览
 *
 * @author yanghua
 */
@RestController
@Response
@RequestMapping("/api")
public class CsrController {
    @Resource
    ApiCsrMapper apiCsrMapper;

    @RequestMapping("/csrs")
    public List<ApiCsr> getAllApis() {
        return apiCsrMapper.selectAll();
    }

    @RequestMapping("/csrs/v")
    public List<GroupByVersion> getApisGroupByVersions() {
        return apiCsrMapper.selectAllGroupByVersion();
    }

    @RequestMapping("/csrs/b")
    public List<GroupByBusinessArea> getApisGroupByBusinessArea() {
        return apiCsrMapper.selectAllGroupByBusinessArea();
    }
}
