package com.shenbianys.assisant.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 转发配置数据比较（xt_zfpz表）
 *
 * @author Yang Hua
 */
@Controller
@RequestMapping("/api")
public class ZfpzController extends BaseController {
    @RequestMapping("/zfpz/{all}")
    @ResponseBody
    public List<Map<String, Object>> getGnsqInfo(@PathVariable String all) throws ExecutionException, InterruptedException {
        String sql = "select path, ms from xt_zfpz order by path";
        return getCompareResultMapList(sql, "path", "all".equals(all));
    }
}
