package com.shenbianys.assisant.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("/api")
public class BbghController extends BaseController {
    @RequestMapping("/bbgh/{all}")
    @ResponseBody
    public List<Map<String, Object>> getGnsqInfo(@PathVariable String all) throws ExecutionException, InterruptedException {
        String sql = "SELECT bbmc as md5, bbmc FROM `fw_bbgh` order by bbmc asc";
        return getCompareResultMapList(sql, "md5", "all".equals(all));
    }
}
