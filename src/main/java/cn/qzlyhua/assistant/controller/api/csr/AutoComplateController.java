package cn.qzlyhua.assistant.controller.api.csr;

import cn.qzlyhua.assistant.dto.csr.Recommendation;
import cn.qzlyhua.assistant.mapper.ApiCsrParamMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yanghua
 */
@RestController
@RequestMapping("/api")
public class AutoComplateController {

    @Resource
    ApiCsrParamMapper apiCsrParamMapper;

    /**
     * 自动完成输入功能
     *
     * @param query
     * @param limit
     * @return
     */
    @GetMapping("/paramKeysRec")
    public List<Recommendation> getParamKeysRecommendation(String query, int limit) {
        return apiCsrParamMapper.getParamKeysRecommendation(query, limit);
    }
}
