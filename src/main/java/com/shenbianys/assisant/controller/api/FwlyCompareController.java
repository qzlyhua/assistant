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
    /**
     * 服务路由配置
     *
     * @return
     */
    @RequestMapping("/ly")
    @ResponseBody
    public List<Map<String, String>> ly() throws ExecutionException, InterruptedException {
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
