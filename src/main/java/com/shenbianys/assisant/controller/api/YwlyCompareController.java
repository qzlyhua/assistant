package com.shenbianys.assisant.controller.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 业务领域比较（fw_ywly表）
 *
 * @author Yang Hua
 */
@Controller
@Slf4j
@RequestMapping("/api")
public class YwlyCompareController extends BaseController {
    @RequestMapping("/ywly/{all}")
    @ResponseBody
    public List<Map<String, Object>> getGnsqInfo(@PathVariable String all) throws ExecutionException, InterruptedException {
        String sql = "SELECT UPPER(MD5(CONCAT(yylx,ywlyjb,ywlymc))) as md5,yylx,ywlyjb,ywlymc FROM `fw_ywly` order by yylx, ywlyjb, ywlymc";
        return getCompareResultMapList(sql, "md5", "all".equals(all));
    }
}
