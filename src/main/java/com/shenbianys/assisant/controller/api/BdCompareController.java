package com.shenbianys.assisant.controller.api;

import com.shenbianys.assisant.dto.BdCompareDTO;
import com.shenbianys.assisant.dto.GnsqCompareDTO;
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
 * 表单比较（bd_bd表）
 *
 * @author Yang Hua
 */
@Controller
public class BdCompareController extends BaseController {
    @RequestMapping("/bd/{envA}/{envB}")
    @ResponseBody
    public Map<String, Map<String, Map<String, Object>>> compareBd(
            @PathVariable String envA, @PathVariable String envB) {
        String sql = "SELECT bdbh, bdmc FROM bd_bd ORDER BY bdbh ASC";
        List<Map<String, Object>> listA = queryForList(envA, sql);
        List<Map<String, Object>> listB = queryForList(envB, sql);
        Map<String, Map<String, Object>> a2b = getCompareResultOBd(listA, listB);
        Map<String, Map<String, Object>> b2a = getCompareResultOBd(listB, listA);
        Map<String, Map<String, Map<String, Object>>> res = new HashMap<>(2);
        res.put(envA + "有" + envB + "没有", a2b);
        res.put(envB + "有" + envA + "没有", b2a);
        return res;
    }

    private Map<String, Map<String, Object>> getCompareResultOBd(
            List<Map<String, Object>> listA, List<Map<String, Object>> listB) {
        Map<String, Map<String, Object>> map = new HashMap<>();
        for (int i = 0; i < listA.size(); i++) {
            String bdbh = String.valueOf(listA.get(i).get("bdbh"));
            map.put(bdbh, listA.get(i));
        }

        for (int i = 0; i < listB.size(); i++) {
            String bdbh = String.valueOf(listB.get(i).get("bdbh"));
            map.remove(bdbh);
        }

        return map;
    }

    @RequestMapping("/bd/{all}")
    @ResponseBody
    public List<BdCompareDTO> getGnsqInfo(@PathVariable String all) {
        String sql = "SELECT bdbh, bdmc, yylx FROM bd_bd order by bdbh";
        List<Map<String, Object>> listDev = queryForList("dev", sql);
        List<Map<String, Object>> listTest = queryForList("test", sql);
        List<Map<String, Object>> listTesttjd = queryForList("testtjd", sql);
        List<Map<String, Object>> listPro = queryForList("pro", sql);

        Map<String, BdCompareDTO> map = new HashMap<>(listDev.size());
        for (int i = 0; i < listDev.size(); i++) {
            String bdbh = String.valueOf(listDev.get(i).get("bdbh"));
            map.put(bdbh, new BdCompareDTO("dev", listDev.get(i)));
        }

        addListToMap(map, listTest, "test");
        addListToMap(map, listTesttjd, "testtjd");
        addListToMap(map, listPro, "pro");
        List<BdCompareDTO> resList = map.values().stream().collect(Collectors.toList());

        // 移除各环境已经都配置的数据
        if (!"all".equals(all)) {
            for (Iterator<BdCompareDTO> iter = resList.listIterator(); iter.hasNext(); ) {
                BdCompareDTO dto = iter.next();
                if (dto.isAllSet()) {
                    iter.remove();
                }
            }
        }
        return resList;
    }

    private void addListToMap(Map<String, BdCompareDTO> map, List<Map<String, Object>> list, String env) {
        for (int i = 0; i < list.size(); i++) {
            String bdbh = String.valueOf(list.get(i).get("bdbh"));
            if (map.containsKey(bdbh)) {
                BdCompareDTO dto = map.get(bdbh);
                dto.setEnv(env);
            } else {
                map.put(bdbh, new BdCompareDTO(env, list.get(i)));
            }
        }
    }
}
