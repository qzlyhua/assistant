package com.shenbianys.assisant.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 表单比较（bd_bd表）
 *
 * @author Yang Hua
 */
@Controller
@RequestMapping("/api")
public class BdCompareController extends BaseController {
    @RequestMapping("/bd/{all}")
    @ResponseBody
    public List<Map<String, Object>> getGnsqInfo(@PathVariable String all) {
        String sql = "SELECT bdbh, bdmc, yylx FROM bd_bd order by bdbh";
        return getCompareResultMapList(sql, "bdbh", "all".equals(all));
    }
}
