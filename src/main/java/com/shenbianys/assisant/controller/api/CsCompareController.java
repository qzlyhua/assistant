package com.shenbianys.assisant.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 参数比较（xt_ywcs表）
 *
 * @author Yang Hua
 */
@Controller
public class CsCompareController extends BaseController {
    @RequestMapping("/cs/{envA}/{yhyA}/{envB}/{yhyB}")
    @ResponseBody
    public Map<String, Map<String, Map<String, Object>>> compareCs(
            @PathVariable String envA, @PathVariable String yhyA,
            @PathVariable String envB, @PathVariable String yhyB) {
        List<Map<String, Object>> listA = queryForList(envA, genSqlByYhy(yhyA));
        List<Map<String, Object>> listB = queryForList(envB, genSqlByYhy(yhyB));
        Map<String, Map<String, Object>> a2b = getCompareResultOCs(listA, listB);
        Map<String, Map<String, Object>> b2a = getCompareResultOCs(listB, listA);
        Map<String, Map<String, Map<String, Object>>> res = new HashMap<>(2);
        res.put(getTip(envA, yhyA) + "有" + getTip(envB, yhyB) + "没有", a2b);
        res.put(getTip(envB, yhyB) + "有" + getTip(envA, yhyA) + "没有", b2a);
        return res;
    }

    private String getTip(String env, String yhy) {
        return env + "环境" + yhy + "用户域";
    }

    private String genSqlByYhy(String yhybh) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT appcode, csmc FROM xt_ywcs ");
        sqlBuilder.append("WHERE jgbh = ");
        sqlBuilder.append(yhybh);
        sqlBuilder.append(" ORDER BY appcode ASC");
        return sqlBuilder.toString();
    }

    private Map<String, Map<String, Object>> getCompareResultOCs(
            List<Map<String, Object>> listA, List<Map<String, Object>> listB) {
        Map<String, Map<String, Object>> map = new HashMap<>();
        for (int i = 0; i < listA.size(); i++) {
            String appcode = String.valueOf(listA.get(i).get("appcode"));
            map.put(appcode, listA.get(i));
        }

        for (int i = 0; i < listB.size(); i++) {
            String appcode = String.valueOf(listB.get(i).get("appcode"));
            map.remove(appcode);
        }

        return map;
    }
}
