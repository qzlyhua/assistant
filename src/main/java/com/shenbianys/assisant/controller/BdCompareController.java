package com.shenbianys.assisant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表单比较（bd_bd表）
 *
 * @author Yang Hua
 */
@Controller
public class BdCompareController extends BaseController {
    @RequestMapping("/bd/{envA}/{envB}")
    @ResponseBody
    public Map<String, Map<String, Map<String, Object>>> compareBd(
            @PathVariable String envA, @PathVariable String envB) {
        String sql = "SELECT bdbh, bdmc FROM bd_bd ORDER BY bdbh ASC";
        List<Map<String, Object>> listA = queryForList(envA, sql);
        List<Map<String, Object>> listB = queryForList(envB, sql);
        Map<String, Map<String, Object>> a2b = getCompareResultOBd(listA, listB);
        Map<String, Map<String, Object>> b2a = getCompareResultOBd(listB, listA);
        Map<String, Map<String, Map<String, Object>>> res = new HashMap<>(2);
        res.put(envA + "有" + envB + "没有", a2b);
        res.put(envB + "有" + envA + "没有", b2a);
        return res;
    }

    private Map<String, Map<String, Object>> getCompareResultOBd(
            List<Map<String, Object>> listA, List<Map<String, Object>> listB) {
        Map<String, Map<String, Object>> map = new HashMap<>();
        for (int i = 0; i < listA.size(); i++) {
            String bdbh = String.valueOf(listA.get(i).get("bdbh"));
            map.put(bdbh, listA.get(i));
        }

        for (int i = 0; i < listB.size(); i++) {
            String bdbh = String.valueOf(listB.get(i).get("bdbh"));
            map.remove(bdbh);
        }

        return map;
    }
}
