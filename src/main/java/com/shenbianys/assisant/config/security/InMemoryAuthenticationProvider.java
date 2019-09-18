package com.shenbianys.assisant.config.security;

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
 * @author Yang Hua
 */
@Component
public class InMemoryAuthenticationProvider implements AuthenticationProvider {
    private final String adminName = "root";
    private final String adminPassword = "rootPass！@#";

    /**
     * 根用户拥有全部的权限
     */
    private final List<GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_ADMIN"),
            new SimpleGrantedAuthority("ROLE_USER")
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
