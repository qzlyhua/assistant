package com.shenbianys.assisant.controller.web;

import com.shenbianys.assisant.config.security.SecurityConfig;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

/**
 * @author Yang Hua
 */
@Controller
public class WebController {
    @ModelAttribute
    public void addRoleInfo(Model model) {
        Set<String> roles = AuthorityUtils.authorityListToSet(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        model.addAttribute("isAdmin", roles.contains(SecurityConfig.ROLE_ADMIN));
    }

    /**
     * 首页-服务清单
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/", "/fwqd"})
    public String admin(Model model) {
        return "admin/fwqd";
    }

}
