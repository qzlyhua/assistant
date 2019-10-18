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

    @RequestMapping("logStatisticsTask/{env}/{day}")
    public String doStatistics(@PathVariable String env, @PathVariable String day) throws InvocationTargetException, IntrospectionException, ParseException, IllegalAccessException {
        logStatisticsTask.doStatistics(env, day);
        return env + "环境" + day + "日志统计任务触发成功";
    }

    @RequestMapping("/timesByYhymcAndFwmc/{day}")
    public Map<String, Object> callTimes(@PathVariable String day) {
        Map<String, Object> res = new HashMap<>(3);

        String yhymcsSql = "select DISTINCT(yhymc) as yhymc from xt_fwdytj where yhymc != '身边医生'";
        String fwmcsSql = "select DISTINCT(fwmc) as fwmc from xt_fwdytj";
        String dataSql = "select concat(yhymc,'_',fwmc)as search, SUM(dycs) as dyzcs from xt_fwdytj where yhymc != '身边医生' " +
                "and tjsj = '" + day + "' group by fwmc, yhybh order by yhybh, dyzcs desc";

        List<Map<String, Object>> yhymcList = queryForList("dev", yhymcsSql);
        List<Map<String, Object>> fwmcList = queryForList("dev", fwmcsSql);
        List<Map<String, Object>> dataList = queryForList("dev", dataSql);

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


        res.put("yhymcs", yhymcs);
        res.put("fwmcs", fwmcs);
        res.put("data", series);
        res.put("title", day + " 各服务方法调用次数");
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
}
