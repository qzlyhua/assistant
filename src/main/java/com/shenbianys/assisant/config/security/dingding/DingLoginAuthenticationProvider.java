package com.shenbianys.assisant.config.security.dingding;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Date;

/**
 * 钉钉用户认证
 *
 * @author Yang Hua
 */
@Component
@Slf4j
public class DingLoginAuthenticationProvider implements AuthenticationProvider {
    @Value("${assisant.dingding.login.appSecret}")
    private String appSecret;

    @Value("${assisant.dingding.login.appid}")
    private String appId;

    @Value("${assisant.dingding.login.openid}")
    private String openidsAllowed;

    private AccessToken accessToken = new AccessToken();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!this.supports(authentication.getClass())) {
            return null;
        } else if (!isMatch(authentication)) {
            throw new BadCredentialsException("DingDing credentials is not allowed.");
        } else {
            authentication.setAuthenticated(true);
            return authentication;
        }
    }

    private boolean isMatch(Authentication authentication) {
        String code = (String) authentication.getPrincipal();
        String state = (String) authentication.getCredentials();

        log.info("钉钉扫码登录返回code:{}，state:{}", code, state);

        String token = getAccessToken();
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
                log.info("钉钉登录：{}", userInfo);

                String openid = userInfo.getString("openid");
                log.info("允许的钉钉用户列表：[{}]，当前用户：{}", openidsAllowed, openid);
                return openidsAllowed.contains(openid);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return DingLoginAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * 获取钉钉接口访问权限：AccessToken
     * <p>
     * TODO Token缓存
     *
     * @return
     */
    private String getAccessToken() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, -1);
        if (accessToken.getToken() == null || accessToken.getTime.before(calendar.getTime())) {
            StringBuffer url = new StringBuffer();
            url.append("https://oapi.dingtalk.com/sns/gettoken");
            url.append("?appid=");
            url.append(appId);
            url.append("&appsecret=");
            url.append(appSecret);

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
}
