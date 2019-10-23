package com.shenbianys.assistant.controller.api.statistics;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shenbianys.assistant.annotation.response.StandardResponse;
import com.shenbianys.assistant.async.LogStatisticsTask;
import com.shenbianys.assistant.controller.api.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务调用次数统计
 *
 * @author Yang Hua
 */
@RestController
@StandardResponse
@Slf4j
@RequestMapping("/api")
public class CallTimesController extends BaseController {
    @Autowired
    LogStatisticsTask logStatisticsTask;

    @RequestMapping("task/{env}/{day}")
    public String doStatistics(@PathVariable String env, @PathVariable String day) throws InvocationTargetException, IntrospectionException, ParseException, IllegalAccessException {
        logStatisticsTask.doStatistics(env, day);
        return env + "环境" + day + "日志统计任务触发成功";
    }

    /**
     * 未启用
     *
     * @return
     */
    @RequestMapping("/timesByYhymcAndFwmc")
    public Map<String, Object> callTimes() {
        Map<String, Object> res = new HashMap<>(3);

        String baseSql = "select concat(yhymc,'_',fwmc)as search, yhymc, fwmc, SUM(dycs) as dyzcs from " +
                "xt_fwdytj where yhymc != '身边医生'  group by fwmc, yhybh order by dyzcs desc limit 300";
        String yhymcsSql = "select DISTINCT(t.yhymc) as yhymc from (" + baseSql + ") t";
        String fwmcsSql = "select DISTINCT(t.fwmc) as fwmc from (" + baseSql + ") t";

        List<Map<String, Object>> yhymcList = queryForList("dev", yhymcsSql);
        List<Map<String, Object>> fwmcList = queryForList("dev", fwmcsSql);
        List<Map<String, Object>> dataList = queryForList("dev", baseSql);

        // 用户域名称
        List<String> yhymcs = new ArrayList<>(yhymcList.size());
        for (int i = 0; i < yhymcList.size(); i++) {
            yhymcs.add((String) yhymcList.get(i).get("yhymc"));
        }

        // 服务名称
        List<String> fwmcs = new ArrayList<>(fwmcList.size());
        for (int i = 0; i < fwmcList.size(); i++) {
            fwmcs.add((String) fwmcList.get(i).get("fwmc"));
        }

        Map<String, String> data = new HashMap<>(dataList.size());
        for (int i = 0; i < dataList.size(); i++) {
            Map<String, Object> map = dataList.get(i);
            String key = (String) map.get("search");
            String dyzcs = String.valueOf(map.get("dyzcs"));
            data.put(key, dyzcs);
        }

        JSONArray series = new JSONArray();
        for (String yhymc : yhymcs) {
            JSONObject jsonObject = new JSONObject();
            JSONObject j = new JSONObject();
            j.put("show", true);
            j.put("position", "insideRight");

            JSONObject o = new JSONObject();
            o.put("normal", j);

            jsonObject.put("type", "bar");
            jsonObject.put("stack", "总量");
            jsonObject.put("label", o);
            jsonObject.put("name", yhymc);
            jsonObject.put("data", getData(data, fwmcs, yhymc));

            series.add(jsonObject);
        }

        String days = "SELECT CONCAT('统计日期范围：', min(tjsj),' 至 ',max(tjsj)) as c FROM `xt_fwdytj`";
        Map<String, Object> d = queryForMap("dev", days);

        res.put("yhymcs", yhymcs);
        res.put("fwmcs", fwmcs);
        res.put("data", series);
        res.put("title", d.get("c"));
        return res;
    }

    private List<String> getData(Map<String, String> data, List<String> fwmcs, String yhymc) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < fwmcs.size(); i++) {
            String fwmc = fwmcs.get(i);
            String key = yhymc + "_" + fwmc;
            String c = data.containsKey(key) ? data.get(key) : "0";
            result.add(c);
        }
        return result;
    }

    /**
     * 接口调用数量统计 - 根据服务名称分组
     *
     * @return
     */
    @RequestMapping("/timesGroupByFwmc")
    public List<Map<String, Object>> timesGroupByFwmc() {
        String sql = "SELECT t.fwmc, t.dycs, concat(t.dycs / s.dycs * 100, '%') AS bfb FROM " +
                "(SELECT fwmc, sum(dycs) AS dycs FROM xt_fwdytj where dycs != 0  GROUP BY fwmc ORDER BY dycs DESC) t, " +
                "(SELECT sum(dycs) AS dycs FROM xt_fwdytj) s";

        String fwmcDzb = "SELECT fwmc, fwsm FROM fw_qd order by xgsj asc";
        List<Map<String, Object>> fwmcList = queryForList("pro", fwmcDzb);
        Map<String, String> fwmcMap = new HashMap<>(fwmcList.size());
        for (int i = 0; i < fwmcList.size(); i++) {
            Map<String, Object> fw = fwmcList.get(i);
            fwmcMap.put(String.valueOf(fw.get("fwmc")), String.valueOf(fw.get("fwsm")));
        }

        List<Map<String, Object>> res = queryForList("dev", sql);
        for (int i = 0; i < res.size(); i++) {
            Map<String, Object> map = res.get(i);
            String fwmc = String.valueOf(map.get("fwmc"));
            map.put("fwsm", fwmcMap.containsKey(fwmc) ? fwmcMap.get(fwmc) : "");
        }

        return res;
    }

    /**
     * 接口调用数量统计 - 指定服务名称，根据用户域名称分组
     *
     * @return
     */
    @RequestMapping("/timesOfFwmcGroupByYhymc/{fwmc}")
    public List<Map<String, Object>> timesOfFwmcGroupByYhymc(@PathVariable String fwmc) {
        String sql = "SELECT t.yhymc, t.dycs, concat(t.dycs / s.dycs * 100, '%') AS bfb FROM " +
                "(select yhymc, sum(dycs) as dycs from xt_fwdytj where dycs != 0 and fwmc = '" + fwmc + "' GROUP BY yhymc order by dycs desc)t, " +
                "(SELECT sum(dycs) AS dycs FROM xt_fwdytj where fwmc = '" + fwmc + "')s";
        List<Map<String, Object>> res = queryForList("dev", sql);
        return res;
    }

    /**
     * 获取统计数据时间范围
     */
    @RequestMapping("/timeRange")
    public String timeRange() {
        String days = "SELECT CONCAT('统计日期范围：', min(tjsj),' 至 ',max(tjsj)) as c FROM `xt_fwdytj`";
        Map<String, Object> d = queryForMap("dev", days);
        return String.valueOf(d.get("c"));
    }
}