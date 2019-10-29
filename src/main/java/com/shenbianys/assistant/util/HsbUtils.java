package com.shenbianys.assistant.util;

import com.alibaba.fastjson.JSONObject;
import com.shenbianys.assistant.annotation.response.AppException;
import com.shenbianys.assistant.annotation.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

/**
 * @author yangh@winning.com.cn
 */
@Slf4j
public class HsbUtils {
    public static JSONObject getFromHsb(String env, String service) {
        String url = getSsywUrl(env) + "/" + service;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Robot YWRtaW46d2FkYXRh");

        log.info("调用接口：{}", url);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
                    new HttpEntity<>(new LinkedMultiValueMap<>(), headers), JSONObject.class, new HashMap<>(0));
            log.info("调用接口响应：{}", responseEntity.getBody());

            if (200 == responseEntity.getBody().getIntValue("code")) {
                return responseEntity.getBody().getJSONObject("data");
            } else {
                throw new AppException(ResponseCode.RPC_ERROR);
            }
        } catch (Exception e) {
            throw new AppException(e, ResponseCode.RPC_ERROR);
        }
    }

    public static JSONObject postForHsb(String env, String service, JSONObject body) {
        String url = getSsywUrl(env) + "/" + service;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Robot YWRtaW46d2FkYXRh");
        headers.setContentType(MediaType.APPLICATION_JSON);

        log.info("调用接口：{}；入参：{}", url, body);
        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpEntity<JSONObject> httpEntity = new HttpEntity(body, headers);
            ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(url, httpEntity, JSONObject.class);
            log.info("调用接口响应：{}", responseEntity.getBody());

            if (200 == responseEntity.getBody().getIntValue("code")) {
                return responseEntity.getBody().getJSONObject("data");
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
    private static String getSsywUrl(String env) {
        if ("dev".equals(env)) {
            return "http://dev.ssyw.arounddoctor.com/ssyw";
        } else if ("test".equals(env)) {
            return "http://test.ssyw.arounddoctor.com/ssyw/api";
        } else if ("testtjd".equals(env)) {
            return "http://test.tjdssyw.arounddoctor.com/ssyw/api";
        } else if ("pro".equals(env)) {
            return "https://ssyw.arounddoctor.com/ssyw/api";
        }
        return "";
    }
}
