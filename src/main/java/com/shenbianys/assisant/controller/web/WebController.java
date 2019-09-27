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
import org.springframework.web.bind.annotation.PathVariable;
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
     * 首页
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/"})
    public String index(Model model) {
        return "admin/index";
    }

    /**
     * 服务清单
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/fwqd"})
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

    /**
     * 服务路由
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/fwly"})
    public String fwly(Model model) {
        return "admin/fwly";
    }

    /**
     * 服务路由比较
     * fwly/dev_0/dev_001001007011
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/fwly/{a}/{b}"})
    public String fwlyCompare(Model model, @PathVariable String a, @PathVariable String b) {
        model.addAttribute("a", a);
        model.addAttribute("b", b);
        return "admin/fwlyCompare";
    }

    /**
     * 版本规划
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/bbgh"})
    public String bbgh(Model model) {
        return "admin/bbgh";
    }

    /**
     * 服务标签
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/fwbq"})
    public String fwbq(Model model) {
        return "admin/fwbq";
    }

    /**
     * 第三方系统字典
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/dsfxtzd"})
    public String dsfxtzd(Model model) {
        return "admin/dsfxtzd";
    }

    /**
     * 转发配置
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/zfpz"})
    public String zfpz(Model model) {
        return "admin/zfpz";
    }
}
