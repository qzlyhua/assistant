package com.shenbianys.assisant.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yang Hua
 */
@Controller
public class IndexController extends BaseController {

    /**
     * 系统概览
     *
     * @return
     */
    @RequestMapping("/xtgl")
    @ResponseBody
    public List<Map<String, String>> xtgl() {
        String sql = "SELECT '业务领域' as xmmc, count(ywly.ywlybh) as count FROM fw_ywly ywly " +
                "union all " +
                "SELECT '功能授权', count(gnsq.id) FROM sq_gnsq gnsq " +
                "union all " +
                "SELECT '表单列表', count(bd.bdbh) FROM bd_bd bd " +
                "union all " +
                "SELECT '系统参数', count(xtcs.csid) FROM xt_ywcs xtcs where jgbh = '001' " +
                "union all " +
                "SELECT '服务清单', count(fwqd.id) FROM fw_qd fwqd";

        List<Map<String, Object>> dev = queryForList("dev", sql);
        List<Map<String, Object>> test = queryForList("test", sql);
        List<Map<String, Object>> testtjd = queryForList("testtjd", sql);
        List<Map<String, Object>> pro = queryForList("pro", sql);

        Map<String, String> ywly = genMap("业务领域", "ywly");
        Map<String, String> gnsq = genMap("功能授权", "gnsq");
        Map<String, String> bdlb = genMap("表单列表", "bd");
        Map<String, String> xtcs = genMap("系统参数", "xtcs");
        Map<String, String> fwqd = genMap("服务清单", "fwqd");

        doFor("dev", dev, ywly, gnsq, bdlb, xtcs, fwqd);
        doFor("test", test, ywly, gnsq, bdlb, xtcs, fwqd);
        doFor("testtjd", testtjd, ywly, gnsq, bdlb, xtcs, fwqd);
        doFor("pro", pro, ywly, gnsq, bdlb, xtcs, fwqd);

        List<Map<String, String>> res = new ArrayList<>(5);
        res.add(ywly);
        res.add(gnsq);
        res.add(bdlb);
        res.add(xtcs);
        res.add(fwqd);

        return res;
    }

    private Map<String, String> genMap(String xmmc, String url) {
        Map<String, String> map = new HashMap<>(5);
        String a = "<a href='/" + url + "'>" + xmmc + "</a>";
        map.put("xmmc", a);
        return map;
    }

    private void doFor(String env, List<Map<String, Object>> list,
                       Map<String, String> ywly,
                       Map<String, String> gnsq,
                       Map<String, String> bdlb,
                       Map<String, String> xtcs,
                       Map<String, String> fwqd) {
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);
            String xmmc = (String) map.get("xmmc");
            String count = Long.toString((Long) map.get("count"));

            if ("业务领域".equals(xmmc)) {
                ywly.put(env, count);
            } else if ("功能授权".equals(xmmc)) {
                gnsq.put(env, count);
            } else if ("表单列表".equals(xmmc)) {
                bdlb.put(env, count);
            } else if ("系统参数".equals(xmmc)) {
                xtcs.put(env, count);
            } else if ("服务清单".equals(xmmc)) {
                fwqd.put(env, count);
            }
        }
    }
}
