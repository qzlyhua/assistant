package com.shenbianys.assisant.config.security.dingding;

import com.shenbianys.assisant.config.properties.DingDingLoginProperties;
import com.shenbianys.assisant.config.security.SecurityConfig;
import com.shenbianys.assisant.util.DingDingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 钉钉用户认证
 *
 * @author Yang Hua
 */
@Component
@Slf4j
public class DingLoginAuthenticationProvider implements AuthenticationProvider {
    public static final String LOGIN_PREFIX = "LOGIN-";
    public static final String ADD_PREFIX = "ADD-b2d58e8f-3506-44af-90f4-c46fbabc308f";

    @Autowired
    DingDingLoginProperties dingDingLoginProperties;

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

        log.info("钉钉扫码登录 code:{}，state:{}", code, state);
        if (state.startsWith(LOGIN_PREFIX)) {
            String openid = DingDingUtils.getOpenIdByCode(dingDingLoginProperties, code);
            if (dingDingLoginProperties.getAdminOpenid().equals(openid)) {
                return true;
            } else {
                Set<String> users = DingDingUtils.getUsers(dingDingLoginProperties);
                for (String s : users) {
                    if (s.equals(openid)) {
                        return true;
                    }
                }
                return false;
            }
        } else if (ADD_PREFIX.equals(state)) {
            String openid = DingDingUtils.getOpenIdByCode(dingDingLoginProperties, code);
            log.info("新增钉钉用户！openid：{}", openid);
            // 添加openid
            DingDingUtils.addUser(dingDingLoginProperties, openid);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return DingLoginAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
