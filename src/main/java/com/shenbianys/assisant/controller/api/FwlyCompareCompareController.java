package com.shenbianys.assisant.controller.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 路由比较（fw_ly表）
 *
 * @author Yang Hua
 */
@Controller
@RequestMapping("/api")
public class FwlyCompareCompareController extends BaseController {
    /**
     * 服务路由配置
     *
     * @return
     */
    @RequestMapping("/lypz/{a}/{b}/{all}")
    @ResponseBody
    public Map<String, Object> lypzCompare(@PathVariable String a, @PathVariable String b,
                                           @PathVariable String all) throws ExecutionException, InterruptedException {
        String envA = a.split("_")[0];
        String yhyA = a.split("_")[1];
        String envB = b.split("_")[0];
        String yhyB = b.split("_")[1];

        String sqlA = "SELECT CONCAT('" + getEnvChinese(envA) + "-',jgmc) as hjbs, MD5(CONCAT(xtmc,fwmc)) as md5," +
                "jgbh,xtmc,ip,fwmc,fwbh,fwdz,dsffwmc FROM fw_ly where jgbh = '" + yhyA + "' order by xtmc, fwmc";
        String sqlB = "SELECT CONCAT('" + getEnvChinese(envB) + "-',jgmc) as hjbs, MD5(CONCAT(xtmc,fwmc)) as md5," +
                "jgbh,xtmc,ip,fwmc,fwbh,fwdz,dsffwmc FROM fw_ly where jgbh = '" + yhyB + "' order by xtmc, fwmc";

        Future<List<Map<String, Object>>> aList = asyncTask.getList(envA, sqlA);
        Future<List<Map<String, Object>>> bList = asyncTask.getList(envB, sqlB);

        // 排序用列表
        List<String> orderList = new ArrayList<>(aList.get().size());
        // 所有数据的索引map
        Map<String, Map<String, Object>> map = new HashMap<>(aList.get().size());
        addListToMap(orderList, "md5", map, aList.get(), "envA");
        addListToMap(orderList, "md5", map, bList.get(), "envB");

        // 处理结果集
        List<Map<String, Object>> resList = new ArrayList<>(orderList.size());
        for (int i = 0; i < orderList.size(); i++) {
            Map<String, Object> dto = map.get(orderList.get(i));
            if ("all".equals(all)) {
                resList.add(dto);
            } else {
                if (!dto.containsKey("envA") || !dto.containsKey("envB")) {
                    resList.add(dto);
                }
            }
        }

        Map<String, Object> resMap = new HashMap<>();
        resMap.put("result", resList);
        resMap.put("envA", aList.get().get(0).get("hjbs"));
        resMap.put("envB", bList.get().get(0).get("hjbs"));
        return resMap;
    }

    private String getEnvChinese(String en) {
        if ("dev".equals(en)) {
            return "开发";
        } else if ("test".equals(en)) {
            return "测试";
        } else if ("testtjd".equals(en)) {
            return "突击队";
        } else if ("pro".equals(en)) {
            return "生产";
        } else {
            return "";
        }
    }
}
