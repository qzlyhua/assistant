package com.shenbianys.assisant.controller.login;

import org.springframework.beans.factory.annotation.Value;
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
public class WebLoginController {
    @Value("${assisant.dingding.login.callback}")
    private String callbackUrl;

    @Value("${assisant.dingding.login.appid}")
    private String appId;

    @RequestMapping(value = {"/login"})
    public String login(Model model) throws UnsupportedEncodingException {
        String callback = URLEncoder.encode(callbackUrl, "utf-8");
        String state = UUID.randomUUID().toString();
        StringBuffer url = new StringBuffer();
        url.append("https://oapi.dingtalk.com/connect/qrconnect?appid=");
        url.append(appId);
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
