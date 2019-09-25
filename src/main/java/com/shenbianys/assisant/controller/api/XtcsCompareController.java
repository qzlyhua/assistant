package com.shenbianys.assisant.controller.api;

import com.shenbianys.assisant.dto.XtcsCompareDTO;
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
 * 系统参数比较（xt_ywcs表）
 *
 * @author Yang Hua
 */
@Controller
@Slf4j
public class XtcsCompareController extends BaseController {
    @RequestMapping("/xtcs/{envA}/{envB}")
    @ResponseBody
    public Map<String, Map<String, Map<String, Object>>> compareFw(
            @PathVariable String envA, @PathVariable String envB) {
        String sql = "SELECT appcode,csmc,csms,xgsj from xt_ywcs where jgbh = '001' order by appcode";
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
            String md5 = String.valueOf(listA.get(i).get("appcode"));
            map.put(md5, listA.get(i));
        }

        for (int i = 0; i < listB.size(); i++) {
            String md5 = String.valueOf(listB.get(i).get("appcode"));
            map.remove(md5);
        }

        return map;
    }

    @RequestMapping("/xtcs/{all}")
    @ResponseBody
    public List<XtcsCompareDTO> getGnsqInfo(@PathVariable String all) {
        String sql = "SELECT appcode,csmc,csms,xgsj from xt_ywcs where jgbh = '001' order by appcode";
        List<Map<String, Object>> listDev = queryForList("dev", sql);
        List<Map<String, Object>> listTest = queryForList("test", sql);
        List<Map<String, Object>> listTesttjd = queryForList("testtjd", sql);
        List<Map<String, Object>> listPro = queryForList("pro", sql);

        Map<String, XtcsCompareDTO> map = new HashMap<>(listDev.size());
        for (int i = 0; i < listDev.size(); i++) {
            String appcode = String.valueOf(listDev.get(i).get("appcode"));
            map.put(appcode, new XtcsCompareDTO("dev", listDev.get(i)));
        }

        addListToMap(map, listTest, "test");
        addListToMap(map, listTesttjd, "testtjd");
        addListToMap(map, listPro, "pro");
        List<XtcsCompareDTO> resList = map.values().stream().collect(Collectors.toList());

        // 移除各环境已经都配置的数据
        if (!"all".equals(all)) {
            for (Iterator<XtcsCompareDTO> iter = resList.listIterator(); iter.hasNext(); ) {
                XtcsCompareDTO dto = iter.next();
                if (dto.isAllSet()) {
                    iter.remove();
                }
            }
        }
        return resList;
    }

    private void addListToMap(Map<String, XtcsCompareDTO> map, List<Map<String, Object>> list, String env) {
        for (int i = 0; i < list.size(); i++) {
            String appcode = String.valueOf(list.get(i).get("appcode"));
            if (map.containsKey(appcode)) {
                XtcsCompareDTO dto = map.get(appcode);
                dto.setEnv(env);
            } else {
                map.put(appcode, new XtcsCompareDTO(env, list.get(i)));
            }
        }
    }
}
