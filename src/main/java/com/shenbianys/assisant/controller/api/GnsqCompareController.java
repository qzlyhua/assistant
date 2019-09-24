package com.shenbianys.assisant.controller.api;

import com.shenbianys.assisant.dto.GnsqCompareDTO;
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
 * 功能授权比较（sq_gnsq表）
 *
 * @author Yang Hua
 */
@Controller
@Slf4j
public class GnsqCompareController extends BaseController {
    @RequestMapping("/gnsq/{envA}/{envB}")
    @ResponseBody
    public Map<String, Map<String, Map<String, Object>>> compareFw(
            @PathVariable String envA, @PathVariable String envB) {
        String sql = "SELECT UPPER(MD5(CONCAT(id,dm,mc,sjdm,lx))) as md5,id,dm,mc,sjdm,lx FROM sq_gnsq order by dm asc";
        List<Map<String, Object>> listA = queryForList(envA, sql);
        List<Map<String, Object>> listB = queryForList(envB, sql);
        Map<String, Map<String, Object>> a2b = getCompareResultOfGnsq(listA, listB);
        Map<String, Map<String, Object>> b2a = getCompareResultOfGnsq(listB, listA);
        Map<String, Map<String, Map<String, Object>>> res = new HashMap<>(2);
        res.put(envA + "有" + envB + "没有", a2b);
        res.put(envB + "有" + envA + "没有", b2a);
        return res;
    }

    private Map<String, Map<String, Object>> getCompareResultOfGnsq(
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

    @RequestMapping("/gnsq/{all}")
    @ResponseBody
    public List<GnsqCompareDTO> getGnsqInfo(@PathVariable String all) {
        String sql = "SELECT UPPER(MD5(CONCAT(dm,mc,sjdm,lx))) as md5,id,dm,mc,sjdm,lx FROM sq_gnsq order by dm asc";
        List<Map<String, Object>> listDev = queryForList("dev", sql);
        List<Map<String, Object>> listTest = queryForList("test", sql);
        List<Map<String, Object>> listTesttjd = queryForList("testtjd", sql);
        List<Map<String, Object>> listPro = queryForList("pro", sql);

        Map<String, GnsqCompareDTO> map = new HashMap<>(listDev.size());
        for (int i = 0; i < listDev.size(); i++) {
            String md5 = String.valueOf(listDev.get(i).get("md5"));
            map.put(md5, new GnsqCompareDTO("dev", listDev.get(i)));
        }

        addListToMap(map, listTest, "test");
        addListToMap(map, listTesttjd, "testtjd");
        addListToMap(map, listPro, "pro");
        List<GnsqCompareDTO> resList = map.values().stream().collect(Collectors.toList());

        // 移除各环境已经都配置的数据
        if (!"all".equals(all)) {
            for (Iterator<GnsqCompareDTO> iter = resList.listIterator(); iter.hasNext(); ) {
                GnsqCompareDTO dto = iter.next();
                if (dto.isAllSet()) {
                    iter.remove();
                }
            }
        }
        return resList;
    }

    private void addListToMap(Map<String, GnsqCompareDTO> map, List<Map<String, Object>> list, String env) {
        for (int i = 0; i < list.size(); i++) {
            String md5 = String.valueOf(list.get(i).get("md5"));
            if (map.containsKey(md5)) {
                GnsqCompareDTO dto = map.get(md5);
                dto.setEnv(env);
            } else {
                map.put(md5, new GnsqCompareDTO(env, list.get(i)));
            }
        }
    }
}
