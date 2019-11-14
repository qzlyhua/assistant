package com.shenbianys.assistant.controller.api.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shenbianys.assistant.annotation.response.StandardResponse;
import com.shenbianys.assistant.controller.api.BaseController;
import com.shenbianys.assistant.entity.AreaEntity;
import com.shenbianys.assistant.util.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.beans.IntrospectionException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author yangh@winning.com.cn
 */
@Slf4j
@RestController
@StandardResponse
public class BasicDataSyncController extends BaseController {
    public int areaSync() throws UnsupportedEncodingException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        String url = "http://222.247.54.183:8078/httpapi/services.ashx?data={data}";
        Map<String, String> param = getAreaRequestParamMap(1);
        log.info("发起接口调用：{}，参数：{}", url, param);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class, param);

        JSONArray areaList = JSON.parseObject(responseEntity.getBody()).getJSONArray("responseContent");
        List<AreaEntity> responseData = new ArrayList<>(200);
        log.info("接口调用成功，共{}条行政区划数据", areaList.size());

        log.info("清空 jc_xzqh 表", areaList.size());
        String truncate = "truncate table jc_xzqh";
        mysqlService.update("dev", truncate);

        AreaEntity china = new AreaEntity();
        china.setSfmj(false);
        china.setStamp(String.valueOf(System.currentTimeMillis()));
        china.setXgsj(new Date());
        china.setXgrxm("admin");
        china.setXgrbh("0");
        china.setXzqhbh("000000000000");
        china.setXzqhmc("中国");
        china.setXzqhlx("0");
        china.setXzqhqc("中华人民共和国");
        china.setSjqhbh("");

        log.info("新增根节点：{}", china);
        mysqlService.update("dev", SqlUtils.generatorInsertSql(china));

        for (int i = 0; i < areaList.size(); i++) {
            JSONObject jsonObject = areaList.getJSONObject(i);

            AreaEntity areaEntity = new AreaEntity();
            areaEntity.setXzqhmc(jsonObject.getString("unitname"));
            areaEntity.setXzqhlx(jsonObject.getString("unitlevel"));
            areaEntity.setXzqhqc(jsonObject.getString("fullname"));
            areaEntity.setXzqhbh(jsonObject.getString("addresscode"));
            areaEntity.setSjqhbh(jsonObject.getString("parentid"));
            areaEntity.setXgrbh("1");
            areaEntity.setXgrxm("lianxu");
            areaEntity.setXgsj(new Date());
            areaEntity.setStamp(String.valueOf(System.currentTimeMillis()));
            areaEntity.setSfmj(false);

            responseData.add(areaEntity);
            if (responseData.size() == 1000) {
                int affect = saveAndClear(responseData);
                log.info("正在处理：{}/{}", i + 1, areaList.size());
            }
        }
        if (responseData.size() > 0) {
            int affect = saveAndClear(responseData);
        }
        log.info("处理完成！{}/{}", areaList.size(), areaList.size());

        int u = updateSfmj();
        log.info("更新是否末级标志: {} 条", u);
        log.info("更新完成！");
        return areaList.size();
    }

    private Map<String, String> getAreaRequestParamMap(int page) throws UnsupportedEncodingException {
        Map<String, String> res = new HashMap<>(1);

        JSONObject requestParams = new JSONObject();
        requestParams.put("pageIndex", "");
        requestParams.put("xgsj", "1970-10-11 00:00:00");

        JSONObject data = new JSONObject();
        data.put("securityToken", "");
        data.put("requestTime", "2018-07-20 15:02:33");
        data.put("interfaceType", "JC3_JK01");
        data.put("verifyCode", "");
        data.put("requestId", "201807201502331098799");
        data.put("requestParams", requestParams);
        data.put("actionCode", "JC3_JK01_A01");
        data.put("modularCode", "lianxu_gw");

        res.put("data", data.toJSONString());
        return res;
    }

    private int saveAndClear(List<AreaEntity> areaEntities) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        String sql = SqlUtils.generatorInsertSql(areaEntities);
        int res = mysqlService.update("dev", sql);
        areaEntities.clear();
        return res;
    }

    private int updateSfmj() {
        String sql = "update jc_xzqh t set t.sfmj = 1 where t.xzqhbh not in(select a.sjqhbh from (select sjqhbh from jc_xzqh) as a)";
        return mysqlService.update("dev", sql);
    }

    /**
     * @return
     */
    @RequestMapping("/areaDataSync")
    public int areaDataSync() throws InvocationTargetException, IntrospectionException, IllegalAccessException, UnsupportedEncodingException {
        int total = areaSync();
        return total;
    }
}
