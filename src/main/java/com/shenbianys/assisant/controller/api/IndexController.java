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
@RequestMapping("/api")
public class IndexController extends BaseController {

    /**
     * 系统概览
     *
     * @return
     */
    @RequestMapping("/xtgl")
    @ResponseBody
    public List<Map<String, String>> xtgl() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT '业务领域' as xmmc, count(ywly.ywlybh) as count FROM fw_ywly ywly");
        sql.append(" UNION ALL ").append("SELECT '功能授权', count(gnsq.id) FROM sq_gnsq gnsq");
        sql.append(" UNION ALL ").append("SELECT '表单列表', count(bd.bdbh) FROM bd_bd bd");
        sql.append(" UNION ALL ").append("SELECT '系统参数', count(xtcs.csid) FROM xt_ywcs xtcs where jgbh = '001'");
        sql.append(" UNION ALL ").append("SELECT '服务清单', count(fwqd.id) FROM fw_qd fwqd");
        sql.append(" UNION ALL ").append("SELECT '版本规划', count(bbgh.id) FROM fw_bbgh bbgh");
        sql.append(" UNION ALL ").append("SELECT '服务标签', count(fwbq.fwbqid) FROM fw_bq fwbq");
        sql.append(" UNION ALL ").append("SELECT '系统字典', count(dsfxtzd.zdid) FROM xt_dsfzd dsfxtzd");
        sql.append(" UNION ALL ").append("SELECT '转发配置', count(zfpz.path) FROM xt_zfpz zfpz");
        sql.append(" UNION ALL ").append("SELECT '服务路由', count(ly.lybh) FROM fw_ly ly");

        Map<String, List<Map<String, Object>>> resListMap = queryForListFromAll(sql.toString());

        Map<String, String> ywly = genMap("业务领域", "ywly");
        Map<String, String> gnsq = genMap("功能授权", "gnsq");
        Map<String, String> bdlb = genMap("表单列表", "bd");
        Map<String, String> xtcs = genMap("系统参数", "xtcs");
        Map<String, String> fwqd = genMap("服务清单", "fwqd");
        Map<String, String> bbgh = genMap("版本规划", "bbgh");
        Map<String, String> fwbq = genMap("服务标签", "fwbq");
        Map<String, String> dsfxtzd = genMap("系统字典", "dsfxtzd");
        Map<String, String> zfpz = genMap("转发配置", "zfpz");
        Map<String, String> fwly = genMap("服务路由", "fwly");

        doFor("dev", resListMap.get("dev"), ywly, gnsq, bdlb, xtcs, fwqd, bbgh, fwbq, dsfxtzd, zfpz, fwly);
        doFor("test", resListMap.get("test"), ywly, gnsq, bdlb, xtcs, fwqd, bbgh, fwbq, dsfxtzd, zfpz, fwly);
        doFor("testtjd", resListMap.get("testtjd"), ywly, gnsq, bdlb, xtcs, fwqd, bbgh, fwbq, dsfxtzd, zfpz, fwly);
        doFor("pro", resListMap.get("pro"), ywly, gnsq, bdlb, xtcs, fwqd, bbgh, fwbq, dsfxtzd, zfpz, fwly);

        List<Map<String, String>> res = new ArrayList<>(5);
        res.add(ywly);
        res.add(gnsq);
        res.add(bdlb);
        res.add(xtcs);
        res.add(fwqd);
        res.add(bbgh);
        res.add(fwbq);
        res.add(dsfxtzd);
        res.add(zfpz);
        res.add(fwly);

        return res;
    }

    private Map<String, String> genMap(String xmmc, String url) {
        Map<String, String> map = new HashMap<>(5);
        String a = "<a href='/" + url + "'>" + xmmc + "</a>";
        map.put("xmmc", a);
        return map;
    }

    private void doFor(String env, List<Map<String, Object>> list,
                       Map<String, String> ywly, Map<String, String> gnsq, Map<String, String> bdlb,
                       Map<String, String> xtcs, Map<String, String> fwqd, Map<String, String> bbgh,
                       Map<String, String> fwbq, Map<String, String> dsfxtzd, Map<String, String> zfpz,
                       Map<String, String> fwly) {
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
            } else if ("版本规划".equals(xmmc)) {
                bbgh.put(env, count);
            } else if ("服务标签".equals(xmmc)) {
                fwbq.put(env, count);
            } else if ("系统字典".equals(xmmc)) {
                dsfxtzd.put(env, count);
            } else if ("转发配置".equals(xmmc)) {
                zfpz.put(env, count);
            } else if ("服务路由".equals(xmmc)) {
                fwly.put(env, count);
            }
        }
    }
}
