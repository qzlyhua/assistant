package com.shenbianys.assisant.controller.web;

import com.shenbianys.assisant.config.properties.DingDingLoginProperties;
import com.shenbianys.assisant.config.security.SecurityConfig;
import com.shenbianys.assisant.config.security.dingding.DingLoginAuthenticationProvider;
import com.shenbianys.assisant.util.DingDingUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    DingDingLoginProperties dingDingLoginProperties;

    @ModelAttribute
    public void addRoleInfo(Model model) {
        Set<String> roles = AuthorityUtils.authorityListToSet(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        boolean isAdmin = roles.contains(SecurityConfig.ROLE_ADMIN);
        model.addAttribute("isAdmin", isAdmin);

        if (isAdmin) {
            String state = DingLoginAuthenticationProvider.ADD_PREFIX;
            String url = DingDingUtils.getUrl(dingDingLoginProperties, state);
            model.addAttribute("url", url);
        }
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
