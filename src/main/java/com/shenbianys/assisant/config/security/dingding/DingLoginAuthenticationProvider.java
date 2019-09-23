package com.shenbianys.assisant.config.security.dingding;

import com.shenbianys.assisant.config.DingDingLoginProperties;
import com.shenbianys.assisant.util.DingDingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * 钉钉用户认证
 *
 * @author Yang Hua
 */
@Component
@Slf4j
public class DingLoginAuthenticationProvider implements AuthenticationProvider {
    public static final String LOGIN_PREFIX = "LOGIN-";

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
            return dingDingLoginProperties.getAdminOpenid().equals(openid);
        } else {
            return false;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return DingLoginAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
