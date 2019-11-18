package com.shenbianys.assistant.controller.api.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shenbianys.assistant.annotation.response.StandardResponse;
import com.shenbianys.assistant.controller.api.BaseController;
import com.shenbianys.assistant.entity.OrgEntity;
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
 * 服务机构数据获取
 *
 * @author yangh@winning.com.cn
 */
@Slf4j
@RestController
@StandardResponse
public class FwjgSyncController extends BaseController {
    public int orgSync() throws UnsupportedEncodingException, IllegalAccessException, IntrospectionException, InvocationTargetException {
        String url = "http://222.247.54.183:8078/httpapi/services.ashx?data={data}";
        Map<String, String> param = getOrgRequestParamMap(1);
        log.info("发起接口调用：{}，参数：{}", url, param);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class, param);

        JSONArray orgList = JSON.parseObject(responseEntity.getBody()).getJSONArray("responseContent");
        log.info("接口调用成功，共{}条医疗机构数据", orgList.size());

        log.info("清空 jc_yljg 表", orgList.size());
        String truncate = "truncate table jc_yljg";
        mysqlService.update("dev", truncate);

        OrgEntity china = new OrgEntity();
//        china.setStamp(String.valueOf(System.currentTimeMillis()));
        china.setXgsj(new Date());
        china.setXgrxm("admin");
        china.setXgrbh("0");
        china.setJgbh("0");
        china.setJgmc("中国");
        china.setSjjgbh(null);
        log.info("新增根节点：{}", china);
        mysqlService.update("dev", SqlUtils.generatorInsertSql(china));

        int initialSize = 1000;
        List<OrgEntity> responseData = new ArrayList<>(initialSize);
        for (int i = 0; i < orgList.size(); i++) {
            JSONObject jsonObject = orgList.getJSONObject(i);

            OrgEntity orgEntity = new OrgEntity();
            orgEntity.setJgbh(jsonObject.getString("orgCode"));
            orgEntity.setJgmc(jsonObject.getString("orgName"));
            orgEntity.setSjjgbh(jsonObject.getString("parentOrgCode"));
            orgEntity.setXgrbh("1");
            orgEntity.setXgrxm("lianxu");
            orgEntity.setXgsj(new Date());
//            orgEntity.setStamp(String.valueOf(System.currentTimeMillis()));

            responseData.add(orgEntity);
            if (responseData.size() == initialSize) {
                int affect = saveAndClear(responseData);
                log.info("正在处理：{}/{}", i + 1, orgList.size());
            }
        }
        if (responseData.size() > 0) {
            int affect = saveAndClear(responseData);
        }
        log.info("处理完成！{}/{}", orgList.size(), orgList.size());
        return orgList.size();
    }

    private Map<String, String> getOrgRequestParamMap(int page) throws UnsupportedEncodingException {
        Map<String, String> res = new HashMap<>(1);

        JSONObject requestParams = new JSONObject();
        requestParams.put("orgType", "");
        requestParams.put("areaCode", "430103000000");
        requestParams.put("parentOrgCode", "");
        requestParams.put("xgsj", "1971-10-11 00:00:00");

        JSONObject data = new JSONObject();
        data.put("securityToken", "");
        data.put("requestTime", "2018-07-20 15:02:33");
        data.put("interfaceType", "JC3_JK01");
        data.put("verifyCode", "");
        data.put("requestId", "201807201502331098799");
        data.put("requestParams", requestParams);
        data.put("actionCode", "JC3_JK01_B01");
        data.put("modularCode", "");

        res.put("data", data.toJSONString());
        return res;
    }

    private int saveAndClear(List<OrgEntity> orgEntities) throws IllegalAccessException, IntrospectionException, InvocationTargetException {
        String sql = SqlUtils.generatorInsertSql(orgEntities);
        int res = mysqlService.update("dev", sql);
        orgEntities.clear();
        return res;
    }

    /**
     * @return
     */
    @RequestMapping("/orgDataSync")
    public int orgDataSync() throws InvocationTargetException, IntrospectionException, IllegalAccessException, UnsupportedEncodingException {
        int total = orgSync();
        return total;
    }
}
