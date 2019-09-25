package com.shenbianys.assisant.controller.api;

import com.shenbianys.assisant.dto.GnsqCompareDTO;
import com.shenbianys.assisant.dto.YwlyCompareDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 业务领域比较（fw_ywly表）
 *
 * @author Yang Hua
 */
@Controller
@Slf4j
public class YwlyCompareController extends BaseController {
    @RequestMapping("/ywly/{envA}/{envB}")
    @ResponseBody
    public Map<String, Map<String, Map<String, Object>>> compareFw(
            @PathVariable String envA, @PathVariable String envB) {
        String sql = "SELECT UPPER(MD5(CONCAT(yylx,ywlyjb,ywlymc))) as md5,yylx,ywlyjb,ywlymc FROM `fw_ywly` order by yylx, ywlyjb, ywlymc";
        List<Map<String, Object>> listA = queryForList(envA, sql);
        List<Map<String, Object>> listB = queryForList(envB, sql);
        Map<String, Map<String, Object>> a2b = getCompareResultOfYwly(listA, listB);
        Map<String, Map<String, Object>> b2a = getCompareResultOfYwly(listB, listA);
        Map<String, Map<String, Map<String, Object>>> res = new HashMap<>(2);
        res.put(envA + "有" + envB + "没有", a2b);
        res.put(envB + "有" + envA + "没有", b2a);
        return res;
    }

    private Map<String, Map<String, Object>> getCompareResultOfYwly(
            List<Map<String, Object>> listA, List<Map<String, Object>> listB) {
        Map<String, Map<String, Object>> map = new HashMap<>();
        for (int i = 0; i < listA.size(); i++) {
            String md5 = String.valueOf(listA.get(i).get("md5"));
            map.put(md5, listA.get(i));
        }

        for (int i = 0; i < listB.size(); i++) {
            String md5 = String.valueOf(listB.get(i).get("md5"));
            map.remove(md5);
        }

        return map;
    }

    @RequestMapping("/ywly/{all}")
    @ResponseBody
    public List<YwlyCompareDTO> getGnsqInfo(@PathVariable String all) {
        String sql = "SELECT UPPER(MD5(CONCAT(yylx,ywlyjb,ywlymc))) as md5,yylx,ywlyjb,ywlymc FROM `fw_ywly` order by yylx, ywlyjb, ywlymc";
        List<Map<String, Object>> listDev = queryForList("dev", sql);
        List<Map<String, Object>> listTest = queryForList("test", sql);
        List<Map<String, Object>> listTesttjd = queryForList("testtjd", sql);
        List<Map<String, Object>> listPro = queryForList("pro", sql);

        Map<String, YwlyCompareDTO> map = new HashMap<>(listDev.size());
        for (int i = 0; i < listDev.size(); i++) {
            String md5 = String.valueOf(listDev.get(i).get("md5"));
            map.put(md5, new YwlyCompareDTO("dev", listDev.get(i)));
        }

        addListToMap(map, listTest, "test");
        addListToMap(map, listTesttjd, "testtjd");
        addListToMap(map, listPro, "pro");
        List<YwlyCompareDTO> resList = map.values().stream().collect(Collectors.toList());

        // 移除各环境已经都配置的数据
        if (!"all".equals(all)) {
            for (Iterator<YwlyCompareDTO> iter = resList.listIterator(); iter.hasNext(); ) {
                YwlyCompareDTO dto = iter.next();
                if (dto.isAllSet()) {
                    iter.remove();
                }
            }
        }
        return resList;
    }

    private void addListToMap(Map<String, YwlyCompareDTO> map, List<Map<String, Object>> list, String env) {
        for (int i = 0; i < list.size(); i++) {
            String md5 = String.valueOf(list.get(i).get("md5"));
            if (map.containsKey(md5)) {
                YwlyCompareDTO dto = map.get(md5);
                dto.setEnv(env);
            } else {
                map.put(md5, new YwlyCompareDTO(env, list.get(i)));
            }
        }
    }
}
