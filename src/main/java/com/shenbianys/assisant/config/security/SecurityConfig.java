package com.shenbianys.assisant.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.util.Arrays;

/**
 * @author Yang Hua
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    InMemoryAuthenticationProvider inMemoryAuthenticationProvider;

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        ProviderManager authenticationManager = new ProviderManager(Arrays.asList(inMemoryAuthenticationProvider));
        return authenticationManager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/login", "/error/**", "/assets/**").permitAll().anyRequest().authenticated()
                .and().formLogin().loginPage("/login").permitAll()
                .and().logout().permitAll()
                .and().sessionManagement().maximumSessions(10).expiredUrl("/login");
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (httpServletRequest, httpServletResponse, authentication) -> {
            try {
                User user = (User) authentication.getPrincipal();
                log.info("USER : {} LOGOUT SUCCESS!", user.getUsername());
            } catch (Exception e) {
                log.error("LOGOUT EXCEPTION : {}", e.getMessage());
            }
            httpServletResponse.sendRedirect("/login");
        };
    }
}
