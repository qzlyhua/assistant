package com.shenbianys.assisant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebController {
    @RequestMapping(value = {"/fwqd", "/"})
    public String admin() {
        return "admin/fwqd";
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
        }
        return pager;
    }
}
