package cn.qzlyhua.assistant.config.security.dingding;

import cn.qzlyhua.assistant.config.properties.DingDingProperties;
import cn.qzlyhua.assistant.dto.DingUser;
import cn.qzlyhua.assistant.entity.User;
import cn.qzlyhua.assistant.mapper.UserMapper;
import cn.qzlyhua.assistant.util.DingDingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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

    final DingDingProperties dingDingLoginProperties;

    @Resource
    UserMapper userMapper;

    public DingLoginAuthenticationProvider(DingDingProperties dingDingLoginProperties) {
        this.dingDingLoginProperties = dingDingLoginProperties;
    }

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
                // 从数据库获取openid
                User user = userMapper.getUserByUsername(openid);
                return user != null;
            }
        } else if (ADD_PREFIX.equals(state)) {
            DingUser user = DingDingUtils.getDingUserByCode(dingDingLoginProperties, code);
            if (userMapper.getUserByUsername(user.getOpenid()) == null) {
                log.info("新增钉钉用户！{}", user);
                userMapper.insertUser(User.builder().nick(user.getNick()).username(user.getOpenid()).type("9").build());
            }
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
