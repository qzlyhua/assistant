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
 * 系统参数比较（xt_ywcs表）
 *
 * @author Yang Hua
 */
@Controller
@Slf4j
@RequestMapping("/api")
public class XtcsCompareController extends BaseController {
    @RequestMapping("/xtcs/{all}")
    @ResponseBody
    public List<Map<String, Object>> getGnsqInfo(@PathVariable String all) throws ExecutionException, InterruptedException {
        String sql = "SELECT appcode,csmc,csms,DATE_FORMAT(xgsj, '%Y-%m-%d %T') as xgsj" +
                " from xt_ywcs where jgbh = '001' order by xgsj desc";
        return getCompareResultMapList(sql, "appcode", "all".equals(all));
    }
}
