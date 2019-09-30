package com.shenbianys.assisant.controller.web;

import com.shenbianys.assisant.config.properties.DingDingLoginProperties;
import com.shenbianys.assisant.config.security.dingding.DingLoginAuthenticationProvider;
import com.shenbianys.assisant.util.DingDingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

/**
 * 登录Controller、错误页面Controller
 *
 * @author Yang Hua
 */
@Controller
@Slf4j
public class LoginController {
    @Autowired
    DingDingLoginProperties dingDingLoginProperties;

    @RequestMapping(value = {"/login"})
    public String login(Model model) {
        String state = DingLoginAuthenticationProvider.LOGIN_PREFIX + UUID.randomUUID();
        String url = DingDingUtils.getUrl(dingDingLoginProperties, state);
        model.addAttribute("url", url);
        return "login/login";
    }

    @RequestMapping(value = "/error/{code}")
    public String error(@PathVariable int code, Model model) {
        String pager = "/500";
        switch (code) {
            case 404:
                model.addAttribute("code", 404);
                pager = "/404";
                break;
            case 401:
                model.addAttribute("code", 500);
                pager = "/401";
                break;
            case 403:
                model.addAttribute("code", 500);
                pager = "/403";
                break;
            default:
                break;
        }
        return pager;
    }
}
