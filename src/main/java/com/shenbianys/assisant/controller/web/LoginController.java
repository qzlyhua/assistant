package com.shenbianys.assisant.controller.web;

import com.shenbianys.assisant.config.DingDingLoginProperties;
import com.shenbianys.assisant.config.security.dingding.DingLoginAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * @author Yang Hua
 */
@Controller
public class LoginController {
    @Autowired
    DingDingLoginProperties dingDingLoginProperties;

    @RequestMapping(value = {"/login"})
    public String login(Model model) throws UnsupportedEncodingException {
        String callback = URLEncoder.encode(dingDingLoginProperties.getCallback(), "utf-8");
        String state = DingLoginAuthenticationProvider.LOGIN_PREFIX + UUID.randomUUID();
        StringBuffer url = new StringBuffer();
        url.append("https://oapi.dingtalk.com/connect/qrconnect?appid=");
        url.append(dingDingLoginProperties.getAppid());
        url.append("&response_type=code&scope=snsapi_login&state=");
        url.append(state);
        url.append("&redirect_uri=");
        url.append(callback);

        model.addAttribute("url", url.toString());
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
