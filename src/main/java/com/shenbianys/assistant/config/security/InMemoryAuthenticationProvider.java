package com.shenbianys.assistant.config.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 简易内置用户
 *
 * @author Yang Hua
 */
@Component
public class InMemoryAuthenticationProvider implements AuthenticationProvider {
    private final String adminName = "root";
    private final String adminPassword = "Na&kOSgpL@66";

    /**
     * 根用户拥有全部的权限
     */
    private final List<GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority(SecurityConfig.ROLE_ADMIN),
            new SimpleGrantedAuthority(SecurityConfig.ROLE_USER),
            new SimpleGrantedAuthority(SecurityConfig.ROLE_DING)
    );

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (isMatch(authentication)) {
            User user = new User(adminName, adminPassword, authorities);
            return new UsernamePasswordAuthenticationToken(user, authentication.getCredentials(), authorities);
        }
        return null;
    }

    private boolean isMatch(Authentication authentication) {
        return authentication.getName().equals(adminName) && authentication.getCredentials().equals(adminPassword);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
