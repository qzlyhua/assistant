package com.shenbianys.assistant.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shenbianys.assistant.config.properties.DingDingLoginProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Yang Hua
 */
@Slf4j
public class DingDingUtils {
    /**
     * 缓存的 AccessToken
     */
    private static AccessToken accessToken = new AccessToken();

    public static String getUrl(DingDingLoginProperties dingDingLoginProperties, String state) {
        try {
            String callback = URLEncoder.encode(dingDingLoginProperties.getCallback(), "utf-8");
            StringBuffer url = new StringBuffer();
            url.append("https://oapi.dingtalk.com/connect/qrconnect?appid=");
            url.append(dingDingLoginProperties.getAppid());
            url.append("&response_type=code&scope=snsapi_login&state=");
            url.append(state);
            url.append("&redirect_uri=");
            url.append(callback);
            return url.toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 根据钉钉扫码登录的回调code获取该用户的openid
     *
     * @param code
     * @return
     */
    public static String getOpenIdByCode(DingDingLoginProperties dingDingLoginProperties, String code) {
        String token = getAccessToken(dingDingLoginProperties);
        if (token == null) {
            throw new BadCredentialsException("钉钉授权失败！");
        }
        StringBuilder url = new StringBuilder();
        url.append("https://oapi.dingtalk.com/sns/getuserinfo_bycode");
        url.append("?access_token=");
        url.append(token);
        url.append("&code=");
        url.append(code);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        JSONObject body = new JSONObject();
        body.put("tmp_auth_code", code);
        body.put("access_token", token);
        HttpEntity<JSONObject> entity = new HttpEntity<>(body, headers);
        ResponseEntity<JSONObject> responseEntity = restTemplate.postForEntity(url.toString(), entity, JSONObject.class);

        if (responseEntity.getStatusCode().equals(HttpStatus.OK)) {
            JSONObject dingResponse = responseEntity.getBody();
            assert dingResponse != null;
            if (0 == dingResponse.getIntValue("errcode")) {
                JSONObject userInfo = dingResponse.getJSONObject("user_info");
                log.info("钉钉用户信息：{}", userInfo);
                String openid = userInfo.getString("openid");
                return openid;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 获取钉钉接口访问权限：AccessToken
     *
     * @return 钉钉接口的 access token
     */
    private static String getAccessToken(DingDingLoginProperties dingDingLoginProperties) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, -1);
        if (accessToken.getToken() == null || accessToken.getTime.before(calendar.getTime())) {
            StringBuffer url = new StringBuffer();
            url.append("https://oapi.dingtalk.com/sns/gettoken");
            url.append("?appid=");
            url.append(dingDingLoginProperties.getAppid());
            url.append("&appsecret=");
            url.append(dingDingLoginProperties.getAppSecret());

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<JSONObject> response = restTemplate.getForEntity(url.toString(), JSONObject.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                int errcode = response.getBody().getIntValue("errcode");
                if (0 == errcode) {
                    String access_token = response.getBody().getString("access_token");
                    accessToken.setToken(access_token);
                    return accessToken.getToken();
                }
            }
            return null;
        } else {
            return accessToken.getToken();
        }
    }

    @Getter
    private static class AccessToken {
        private Date getTime;
        private String token;

        private void setGetTime(Date time) {
            this.getTime = time;
        }

        private void setToken(String token) {
            setGetTime(new Date());
            this.token = token;
        }
    }

    /**
     * 新增钉钉用户到文件内
     */
    public static void addUser(DingDingLoginProperties dingDingLoginProperties, String openid) {
        try {
            File file = new File(dingDingLoginProperties.getUserFilePath());
            if (file.exists()) {
                String t = FileUtils.readFileToString(file);
                JSONArray array = JSONArray.parseArray(t);
                JSONObject o = new JSONObject();
                o.put("openid", openid);
                array.add(o);
                FileUtils.writeStringToFile(file, array.toJSONString(), "UTF-8");
            } else {
                JSONArray array = new JSONArray();
                JSONObject o = new JSONObject();
                o.put("openid", openid);
                array.add(o);
                FileUtils.writeStringToFile(file, array.toJSONString(), "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 从文件内读取钉钉用户
     *
     * @return
     */
    public static Set<String> getUsers(DingDingLoginProperties dingDingLoginProperties) {
        try {
            File file = new File(dingDingLoginProperties.getUserFilePath());
            if (file.exists()) {
                Set<String> set = new HashSet<>();
                String t = FileUtils.readFileToString(file);
                JSONArray array = JSONArray.parseArray(t);
                for (int i = 0; i < array.size(); i++) {
                    JSONObject jsonObject = (JSONObject) array.get(i);
                    set.add(jsonObject.getString("openid"));
                }
                return set;
            } else {
                return new HashSet<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }
}
