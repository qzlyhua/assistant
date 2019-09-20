package com.shenbianys.assisant.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Yang Hua
 */
@Controller
public class WebController {
    @RequestMapping(value = {"/fwqd", "/"})
    public String admin() {
        return "admin/fwqd";
    }
}
