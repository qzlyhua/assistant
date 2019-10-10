package com.shenbianys.assistant.config.security.dingding;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * 钉钉扫码登录的Token-包含code和state两个参数
 *
 * @author Yang Hua
 */
public class DingLoginAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private final Object credentials;

    public DingLoginAuthenticationToken(Object principal, String credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        if (principal != null && !"".equals(principal)) {
            Assert.notEmpty(authorities, "authorities cannot be null or empty");
            this.principal = principal;
            if (credentials != null && !"".equals(credentials)) {
                this.credentials = credentials;
            } else {
                throw new IllegalArgumentException("credentials cannot be null or empty");
            }
        } else {
            throw new IllegalArgumentException("principal cannot be null or empty");
        }
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
