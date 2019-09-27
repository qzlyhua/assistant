package com.shenbianys.assisant.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * 路由比较（fw_ly表）
 *
 * @author Yang Hua
 */
@Controller
@RequestMapping("/api")
public class FwlyCompareController extends BaseController {
    @RequestMapping("/ly/{env}/{yhyA}/{yhyB}")
    @ResponseBody
    public Map<String, Map<String, Set<String>>> compareOne(
            @PathVariable String env, @PathVariable String yhyA, @PathVariable String yhyB) {
        return compare(env, yhyA, env, yhyB);
    }

    @RequestMapping("/ly/{envA}/{yhyA}/{envB}/{yhyB}")
    @ResponseBody
    public Map<String, Map<String, Set<String>>> compareTwo(
            @PathVariable String envA, @PathVariable String yhyA,
            @PathVariable String envB, @PathVariable String yhyB) {
        return compare(envA, yhyA, envB, yhyB);
    }

    private Map<String, Map<String, Set<String>>> compare(String envA, String yhyA, String envB, String yhyB) {
        List<Map<String, Object>> listA = queryForList(envA, genSqlByYhy(yhyA));
        List<Map<String, Object>> listB = queryForList(envB, genSqlByYhy(yhyB));
        Map<String, Set<String>> a2b = getCompareResultOfLy(listA, listB);
        Map<String, Set<String>> b2a = getCompareResultOfLy(listB, listA);
        Map<String, Map<String, Set<String>>> res = new HashMap<>(2);
        res.put(getTip(envA, yhyA) + "有" + getTip(envB, yhyB) + "没有", a2b);
        res.put(getTip(envB, yhyB) + "有" + getTip(envA, yhyA) + "没有", b2a);
        return res;
    }

    private String getTip(String env, String yhy) {
        return env + "环境" + yhy + "用户域";
    }

    private String genSqlByYhy(String yhybh) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT fwmc, xtmc FROM fw_ly ");
        sqlBuilder.append("WHERE jgbh = ");
        sqlBuilder.append(yhybh);
        sqlBuilder.append(" ORDER BY xtmc ASC, fwmc ASC");
        return sqlBuilder.toString();
    }

    private Map<String, Set<String>> getCompareResultOfLy(List<Map<String, Object>> listA, List<Map<String, Object>> listB) {
        Map<String, Map<String, Object>> mapOfA = new HashMap<>(listA.size());
        Set<String> setOfA = new HashSet<>();
        Set<String> setOfB = new HashSet<>();

        for (int i = 0; i < listA.size(); i++) {
            String fwmc = String.valueOf(listA.get(i).get("fwmc"));
            mapOfA.put(fwmc, listA.get(i));
            setOfA.add(fwmc);
        }

        for (int i = 0; i < listB.size(); i++) {
            String fwmc = String.valueOf(listB.get(i).get("fwmc"));
            mapOfA.remove(fwmc);
            setOfB.add(fwmc);
        }

        setOfA.removeAll(setOfB);
        Map<String, Set<String>> res = new HashMap<>(setOfA.size());
        for (String fwmc : setOfA) {
            String xtmc = String.valueOf(mapOfA.get(fwmc).get("xtmc"));
            putRes(res, xtmc, fwmc);
        }

        return res;
    }

    private void putRes(Map<String, Set<String>> res, String xtmc, String fwmc) {
        if (res.containsKey(xtmc)) {
            res.get(xtmc).add(fwmc);
        } else {
            Set<String> set = new HashSet<>();
            set.add(fwmc);
            res.put(xtmc, set);
        }
    }

    /**
     * 服务路由配置
     *
     * @return
     */
    @RequestMapping("/ly")
    @ResponseBody
    public List<Map<String, String>> xtgl() throws ExecutionException, InterruptedException {
        String sql = "select idx.jgbh, ly.jgmc as jgmc, count(ly.lybh) as count from " +
                "(select distinct jgbh from fw_ly order by jgbh) idx left join fw_ly ly " +
                "on idx.jgbh = ly.jgbh " +
                "group by idx.jgbh";
        Map<String, List<Map<String, Object>>> resMap = queryForListFromAll(sql);

        List<Map<String, String>> res = new ArrayList<>();
        addData("dev", "开发环境", resMap.get("dev"), res);
        addData("test", "测试环境", resMap.get("test"), res);
        addData("testtjd", "突击队测试环境", resMap.get("testtjd"), res);
        addData("pro", "生产环境", resMap.get("pro"), res);
        return res;
    }

    private void addData(String env, String envmc, List<Map<String, Object>> list, List<Map<String, String>> res) {
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);

            Map<String, String> m = new HashMap<>(4);
            String jgbh = String.valueOf(map.get("jgbh"));
            String cbx = "<input type=\"checkbox\" id=\"" + env + "_" + jgbh + "\" name=\"cbx\"><label for=\"cbx\"></label>";
            m.put("cbx", cbx);
            m.put("env", env);
            m.put("envmc", envmc);
            m.put("yhybh", jgbh);
            m.put("yhymc", String.valueOf(map.get("jgmc")));
            m.put("count", Long.toString((Long) map.get("count")));
            res.add(m);
        }
    }
}
