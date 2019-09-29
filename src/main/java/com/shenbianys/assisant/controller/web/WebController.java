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
            // TODO 新增用户的标识应当可变
            String state = DingLoginAuthenticationProvider.ADD_PREFIX;
            String url = DingDingUtils.getUrl(dingDingLoginProperties, state);
            // 授权新的钉钉用户，允许其扫码登录
            model.addAttribute("url", url);
        }
    }

    /**
     * 首页
     *
     * @return
     */
    @RequestMapping(value = {"/"})
    public String index() {
        return "admin/index";
    }

    /**
     * 业务领域
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/ywly"})
    public String ywly(Model model) {
        String codes = "idx,yylx,ywlyjb,ywlymc,dev,test,testtjd,pro";
        String[] ths = {"#", "应用类型", "级别", "业务领域名称", "开发", "测试", "突击队", "生产"};
        model.addAttribute("projectName", "业务领域");
        model.addAttribute("tableThs", ths);
        model.addAttribute("tableTdCodes", codes);
        model.addAttribute("urlAll", "/api/ywly/all");
        model.addAttribute("urlDifferent", "/api/ywly/different");
        model.addAttribute("syncUrl", "/api/ywly/sync");
        return "admin/compare";
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

    /**
     * 通用页面-测试
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/compare"})
    public String compare(Model model) {
        String codes = "idx,yylx,ywlyjb,ywlymc,dev,test,testtjd,pro";
        String[] ths = {"#", "应用类型", "级别", "业务领域名称", "开发", "测试", "突击队", "生产"};
        model.addAttribute("projectName", "测试项目名称");
        model.addAttribute("tableThs", ths);
        model.addAttribute("tableTdCodes", codes);
        model.addAttribute("urlAll", "/api/ywly/all");
        model.addAttribute("urlDifferent", "/api/ywly/different");
        model.addAttribute("syncUrl", "/api/ywly/sync");
        return "admin/compare";
    }
}
