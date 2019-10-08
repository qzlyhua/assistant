package com.shenbianys.assisant.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.shenbianys.assisant.controller.api.response.AppException;
import com.shenbianys.assisant.controller.api.response.ResponseCode;
import com.shenbianys.assisant.controller.api.response.StandardResponse;
import com.shenbianys.assisant.entity.ServicePublishEntity;
import com.shenbianys.assisant.entity.ServiceRoutingConfigEntity;
import com.shenbianys.assisant.util.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 路由比较（fw_ly表）
 *
 * @author Yang Hua
 */
@RestController
@StandardResponse
@Slf4j
@RequestMapping("/api")
public class FwlyCompareController extends BaseController {
    @Autowired
    RestTemplate restTemplate;

    /**
     * 服务-路由配置-总览
     *
     * @return
     */
    @RequestMapping("/lypz")
    public List<Map<String, String>> lypz() throws ExecutionException, InterruptedException {
        String sql = "select idx.jgbh, ly.jgmc as jgmc, count(ly.lybh) as count from " +
                "(select distinct jgbh from fw_ly order by jgbh) idx left join fw_ly ly " +
                "on idx.jgbh = ly.jgbh group by idx.jgbh";
        Map<String, List<Map<String, Object>>> resMap = queryForListFromAll(sql);

        List<Map<String, String>> res = new ArrayList<>();
        addData("dev", "开发环境", resMap.get("dev"), res);
        addData("test", "测试环境", resMap.get("test"), res);
        addData("testtjd", "突击队测试环境", resMap.get("testtjd"), res);
        addData("pro", "生产环境", resMap.get("pro"), res);
        return res;
    }

    private void addData(String env, String envmc, List<Map<String, Object>> list, List<Map<String, String>> res) {
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = list.get(i);

            Map<String, String> m = new HashMap<>(4);
            String jgbh = String.valueOf(map.get("jgbh"));
            String cbx = "<input type=\"checkbox\" id=\"" + env + "_" + jgbh + "\" name=\"cbx\"><label for=\"cbx\"></label>";
            m.put("cbx", cbx);
            m.put("env", env);
            m.put("envmc", envmc);
            m.put("yhybh", jgbh);
            m.put("yhymc", String.valueOf(map.get("jgmc")));
            m.put("count", Long.toString((Long) map.get("count")));
            res.add(m);
        }
    }

    /**
     * 服务-路由配置-两用户域比较
     *
     * @return
     */
    @RequestMapping("/lypz/{a}/{b}/{all}")
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

    /**
     * 根据env标识获取易读的中文
     *
     * @param en
     * @return
     */
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

    /**
     * 向目标环境的目标用户域创建指定名称的路由配置
     */
    @RequestMapping("/lypz/sync/{from}/{to}/{fwmc}")
    public int sync(@PathVariable String from, @PathVariable String to, @PathVariable String fwmc) throws Exception {
        log.info("自动配置路由：参考 {} 环境，自动发布 {} 服务到 {} 环境", from, fwmc, to);

        String envFrom = from.split("_")[0];
        String yhyFrom = from.split("_")[1];
        String envTo = to.split("_")[0];
        String yhyTo = to.split("_")[1];

        // 校验源数据-服务发布
        String sqlOfFwfbFromCount = "select count(*) as c from fw_fb where fwmc = '" + fwmc + "' and jgbh = '" + yhyFrom + "'";
        int countOfFwfbFrom = count(envFrom, sqlOfFwfbFromCount);
        Assert.isTrue(countOfFwfbFrom > 0, "源服务发布不存在");

        // 校验源数据-服务路由
        String sqlOfFwlyFromCount = "select count(*) as c from fw_ly where fwmc = '" + fwmc + "' and jgbh = '" + yhyFrom + "'";
        int countOfFwlyFrom = count(envFrom, sqlOfFwlyFromCount);
        Assert.isTrue(countOfFwlyFrom > 0, "源服务路由不存在");

        // 校验目标数据-服务路由
        String sqlOfFwlyToCount = "select count(*) as c from fw_ly where fwmc = '" + fwmc + "' and jgbh = '" + yhyTo + "'";
        int countOfFwlyTo = count(envTo, sqlOfFwlyToCount);
        Assert.isTrue(countOfFwlyTo == 0, "目标服务路由已存在");

        // 源服务路由数据
        String sqlOfFwlyFrom = "select * from fw_ly where fwmc = '" + fwmc + "' and jgbh = '" + yhyFrom + "'";
        ServiceRoutingConfigEntity fwlyFrom = queryForObject(envFrom, sqlOfFwlyFrom, ServiceRoutingConfigEntity.class);

        // 目标第三方系统数据
        String sqlOfDsfxtTo = "select id, xtbs, xtmc, xtdz, jgbh, jgmc from xt_dsfxt where jgbh = '" + yhyTo + "'";
        List<Map<String, Object>> dsfxtMapList = queryForList(envTo, sqlOfDsfxtTo);

        // 校验第三方系统是否存在
        Map<String, String> dsfxtInfo = new HashMap<>();
        for (int i = 0; i < dsfxtMapList.size(); i++) {
            Map<String, Object> dsfxt = dsfxtMapList.get(i);
            if (fwlyFrom.getXtmc().equals(dsfxt.get("xtmc"))) {
                dsfxtInfo.put("xtbh", (String) dsfxt.get("id"));
                dsfxtInfo.put("xtbs", (String) dsfxt.get("xtbs"));
                dsfxtInfo.put("xtmc", (String) dsfxt.get("xtmc"));
                dsfxtInfo.put("ip", (String) dsfxt.get("xtdz"));
                dsfxtInfo.put("jgbh", (String) dsfxt.get("jgbh"));
                dsfxtInfo.put("jgmc", (String) dsfxt.get("jgmc"));

                break;
            }
        }

        Assert.notEmpty(dsfxtInfo, "第三方系统未配置");
        log.info("目标环境第三方系统配置：{}", dsfxtInfo);

        // 校验目标数据-服务发布
        String sqlOfFwfbToCount = "select count(*) as c from fw_fb where fwmc = '" + fwmc + "' and jgbh = '" + yhyTo + "'";
        int fwfbToCount = count(envTo, sqlOfFwfbToCount);

        // 创建目标环境的服务发布
        if (fwfbToCount == 0) {
            String sqlOfFwfbFrom = "select * from fw_fb where fwmc = '" + fwmc + "' and jgbh = '" + yhyFrom + "'";
            ServicePublishEntity fwfbFrom = queryForObject(envFrom, sqlOfFwfbFrom, ServicePublishEntity.class);

            fwfbFrom.setFwfbbh(IdUtils.generator());
            fwfbFrom.setJgbh(dsfxtInfo.get("jgbh"));
            fwfbFrom.setJgmc(dsfxtInfo.get("jgmc"));
            fwfbFrom.setXgsj(new Date());
            fwfbFrom.setXgrbh("-1");
            fwfbFrom.setXgrbh("robot");

            update(envTo, fwfbFrom);

            String sqlOfUpdateFwqd = "update fw_qd set fwzt = '已使用' where fwmc = '" + fwmc + "'";
            update(envTo, sqlOfUpdateFwqd);
        }

        fwlyFrom.setLybh(IdUtils.generator());
        fwlyFrom.setCjsj(new Date());
        fwlyFrom.setCjrbh("-1");
        fwlyFrom.setCjrxm("robot");
        fwlyFrom.setIp(dsfxtInfo.get("ip"));
        fwlyFrom.setJgbh(dsfxtInfo.get("jgbh"));
        fwlyFrom.setJgmc(dsfxtInfo.get("jgmc"));
        fwlyFrom.setXtbh(dsfxtInfo.get("xtbh"));

        update(envTo, fwlyFrom);

        // 调用实施运维接口刷新Redis路由数据
        String url = getSsywUrl(envTo) + "/serviceRouting/sendToRedis?orgCode=" + yhyTo + "&serviceName=" + fwmc;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Robot YWRtaW46d2FkYXRh");

        log.info("调用实施运维接口：{}", url);
        try {
            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
                    new HttpEntity<>(new LinkedMultiValueMap<>(), headers), JSONObject.class, new HashMap<>(0));
            log.info("调用实施运维接口响应：{}", responseEntity.getBody());
            if (200 == responseEntity.getBody().getIntValue("code")) {
                return 1;
            } else {
                throw new AppException(ResponseCode.RPC_ERROR);
            }
        } catch (Exception e) {
            throw new AppException(e, ResponseCode.RPC_ERROR);
        }
    }

    /**
     * 各环境实施运维接口基地址
     *
     * @param env
     * @return
     */
    private String getSsywUrl(String env) {
        if ("dev".equals(env)) {
            return "http://dev.ssyw.arounddoctor.com/ssyw";
        } else if ("test".equals(env)) {
            return "http://test.ssyw.arounddoctor.com/ssyw/api";
        } else if ("testtjd".equals(env)) {
            return "http://test.tjdssyw.arounddoctor.com/ssyw/api";
        } else if ("pro".equals(env)) {
            return "http://ssyw.arounddoctor.com/ssyw/api";
        }
        return "";
    }
}
