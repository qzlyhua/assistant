package com.shenbianys.assisant.config.security.dingding;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 钉钉登录认证Filter
 *
 * @author Yang Hua
 */
public class DingLoginAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
    public static final String SPRING_SECURITY_FORM_STATE_KEY = "state";
    public static final String SPRING_SECURITY_FORM_CODE_KEY = "code";

    private String codeParameter = SPRING_SECURITY_FORM_CODE_KEY;
    private String stateParameter = SPRING_SECURITY_FORM_STATE_KEY;

    public DingLoginAuthenticationProcessingFilter() {
        super(new AntPathRequestMatcher("/dinglogin", "GET"));
    }

    /**
     * 钉钉登录用户拥有 ROLE_DING 权限
     */
    private final List<GrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_DING")
    );

    protected String obtainCode(HttpServletRequest request) {
        return request.getParameter(codeParameter);
    }

    protected String obtainState(HttpServletRequest request) {
        return request.getParameter(stateParameter);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String code = obtainCode(request);
        String state = obtainState(request);
        if (code == null || state == null) {
            throw new InternalAuthenticationServiceException("Failed to get the ding ding params (code, state)");
        }

        AbstractAuthenticationToken authenticationToken = new DingLoginAuthenticationToken(code, state, authorities);
        authenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        return authenticationToken;
    }
}
