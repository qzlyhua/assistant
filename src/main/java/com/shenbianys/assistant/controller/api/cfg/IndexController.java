package com.shenbianys.assistant.controller.api.cfg;

import com.shenbianys.assistant.controller.api.BaseController;
import com.shenbianys.assistant.annotation.response.StandardResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 首页-数据概览
 *
 * @author Yang Hua
 */
@RestController
@StandardResponse
@RequestMapping("/api")
public class IndexController extends BaseController {

    /**
     * 系统概览
     *
     * @return
     */
    @RequestMapping("/xtgl")
    public List<Map<String, String>> xtgl() throws ExecutionException, InterruptedException {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT '业务领域' as xmmc, count(ywly.ywlybh) as count FROM fw_ywly ywly");
        sql.append(" UNION ALL ").append("SELECT '功能授权', count(gnsq.id) FROM sq_gnsq gnsq");
        sql.append(" UNION ALL ").append("SELECT '表单列表', count(bd.bdbh) FROM bd_bd bd");
        sql.append(" UNION ALL ").append("SELECT '系统参数', count(xtcs.csid) FROM xt_ywcs xtcs where jgbh = '001'");
        sql.append(" UNION ALL ").append("SELECT '服务清单', count(fwqd.id) FROM fw_qd fwqd");
        sql.append(" UNION ALL ").append("SELECT '版本规划', count(bbgh.id) FROM fw_bbgh bbgh");
        sql.append(" UNION ALL ").append("SELECT '服务标签', count(fwbq.fwbqid) FROM fw_bq fwbq");
        sql.append(" UNION ALL ").append("SELECT '系统字典', count(xtzd.zdid) FROM xt_dsfzd xtzd");
        sql.append(" UNION ALL ").append("SELECT '转发配置', count(zfpz.path) FROM xt_zfpz zfpz");
        sql.append(" UNION ALL ").append("SELECT '服务路由', count(ly.lybh) FROM fw_ly ly");

        Map<String, List<Map<String, Object>>> dataListMap = queryForListFromAll(sql.toString());
        List<Map<String, String>> res = new ArrayList<>(10);

        String[] xmmcs = {"业务领域", "功能授权", "表单列表", "系统参数", "服务清单", "版本规划", "服务标签", "系统字典", "转发配置", "服务路由"};
        String[] urls = {"ywly", "gnsq", "bd", "xtcs", "fwqd", "bbgh", "fwbq", "dsfxtzd", "zfpz", "fwly"};

        Map<String, String> dataOfDev = listToMap(dataListMap.get("dev"));
        Map<String, String> dataOfTest = listToMap(dataListMap.get("test"));
        Map<String, String> dataOfTesttjd = listToMap(dataListMap.get("testtjd"));
        Map<String, String> dataOfPro = listToMap(dataListMap.get("pro"));

        for (int i = 0; i < xmmcs.length; i++) {
            String xmmc = xmmcs[i];

            String a = "<a href='/" + urls[i] + "'>" + xmmc + "</a>";

            Map<String, String> map = new HashMap<>(5);
            map.put("xmmc", a);
            map.put("dev", dataOfDev.get(xmmc));
            map.put("test", dataOfTest.get(xmmc));
            map.put("testtjd", dataOfTesttjd.get(xmmc));
            map.put("pro", dataOfPro.get(xmmc));

            res.add(map);
        }
        return res;
    }

    /**
     * 数据格式转换，将sql查询结果的 List<Map<String, Object>> 转成 Map<String, String>
     *
     * @param list
     * @return
     */
    private Map<String, String> listToMap(List<Map<String, Object>> list) {
        Map<String, String> map = new HashMap<>(10);
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> selectResMap = list.get(i);
            String xmmc = (String) selectResMap.get("xmmc");
            String count = Long.toString((Long) selectResMap.get("count"));
            map.put(xmmc, count);
        }
        return map;
    }
}
