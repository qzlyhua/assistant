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
    public String fwqd(Model model) {
        return "admin/fwqd";
    }

    /**
     * 功能授权
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/gnsq"})
    public String gnsq(Model model) {
        return "admin/gnsq";
    }

    /**
     * 表单列表
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/bd"})
    public String bd(Model model) {
        return "admin/bd";
    }

    /**
     * 业务领域
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/ywly"})
    public String ywly(Model model) {
        return "admin/ywly";
    }

    /**
     * 系统参数
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/xtcs"})
    public String xtcs(Model model) {
        return "admin/xtcs";
    }
}
