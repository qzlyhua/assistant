package com.shenbianys.assisant.config.security.dingding;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author Yang Hua
 */
@Configuration
public class DingLoginConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    @Override
    public void configure(HttpSecurity http) {
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        DingLoginAuthenticationProvider provider = context.getBean(DingLoginAuthenticationProvider.class);

        http.authenticationProvider(provider);
        DingLoginAuthenticationProcessingFilter filter = new DingLoginAuthenticationProcessingFilter();
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
