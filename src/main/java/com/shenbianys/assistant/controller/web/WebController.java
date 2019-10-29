package com.shenbianys.assistant.controller.web;

import com.shenbianys.assistant.config.properties.DingDingLoginProperties;
import com.shenbianys.assistant.config.security.SecurityConfig;
import com.shenbianys.assistant.config.security.dingding.DingLoginAuthenticationProvider;
import com.shenbianys.assistant.util.DingDingUtils;
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
 * 项目 WEB 页面 MVC Controller
 *
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
     * 功能授权
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/gnsq"})
    public String gnsq(Model model) {
        String codes = "idx,dm,mc,lx,dev,test,testtjd,pro";
        String[] ths = {"#", "代码", "名称", "类型", "开发", "测试", "突击队", "生产"};
        model.addAttribute("projectName", "功能授权");
        model.addAttribute("tableThs", ths);
        model.addAttribute("tableTdCodes", codes);
        model.addAttribute("urlAll", "/api/gnsq/all");
        model.addAttribute("urlDifferent", "/api/gnsq/different");
        model.addAttribute("syncUrl", "/api/gnsq/sync");
        return "admin/compare";
    }

    /**
     * 表单列表
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/bd"})
    public String bd(Model model) {
        String codes = "idx,bdbh,bdmc,yylx,dev,test,testtjd,pro";
        String[] ths = {"#", "表单编号", "表单名称", "应用类型", "开发", "测试", "突击队", "生产"};
        model.addAttribute("projectName", "表单列表");
        model.addAttribute("tableThs", ths);
        model.addAttribute("tableTdCodes", codes);
        model.addAttribute("urlAll", "/api/bd/all");
        model.addAttribute("urlDifferent", "/api/bd/different");
        model.addAttribute("syncUrl", "/api/bd/sync");
        return "admin/compare";
    }

    /**
     * 系统参数
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/xtcs"})
    public String xtcs(Model model) {
        String codes = "idx,appcode,csmc,dev,test,testtjd,pro";
        String[] ths = {"#", "参数代码", "参数名称", "开发", "测试", "突击队", "生产"};
        model.addAttribute("projectName", "系统参数");
        model.addAttribute("tableThs", ths);
        model.addAttribute("tableTdCodes", codes);
        model.addAttribute("urlAll", "/api/xtcs/all");
        model.addAttribute("urlDifferent", "/api/xtcs/different");
        model.addAttribute("syncUrl", "/api/xtcs/sync");
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
        String codes = "idx,fwmc,fwbh,bbh,dev,test,testtjd,pro";
        String titles = "fwmc:fwsm,bbh:xgsj";
        String[] ths = {"#", "服务名称", "服务编号", "版本号", "开发", "测试", "突击队", "生产"};
        model.addAttribute("projectName", "服务清单");
        model.addAttribute("tableThs", ths);
        model.addAttribute("tableTdCodes", codes);
        model.addAttribute("titles", titles);
        model.addAttribute("urlAll", "/api/fwqd/all");
        model.addAttribute("urlDifferent", "/api/fwqd/different");
        model.addAttribute("syncUrl", "/api/fwqd/sync");
        return "admin/compare";
    }

    /**
     * 版本规划
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/bbgh"})
    public String bbgh(Model model) {
        String codes = "idx,bbmc,dev,test,testtjd,pro";
        String[] ths = {"#", "版本名称", "开发", "测试", "突击队", "生产"};
        model.addAttribute("projectName", "版本规划");
        model.addAttribute("tableThs", ths);
        model.addAttribute("tableTdCodes", codes);
        model.addAttribute("urlAll", "/api/bbgh/all");
        model.addAttribute("urlDifferent", "/api/bbgh/different");
        model.addAttribute("syncUrl", "/api/bbgh/sync");
        return "admin/compare";
    }

    /**
     * 服务标签
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/fwbq"})
    public String fwbq(Model model) {
        String codes = "idx,fwbqid,fwbqmc,dev,test,testtjd,pro";
        String[] ths = {"#", "服务标签ID", "服务标签名称", "开发", "测试", "突击队", "生产"};
        model.addAttribute("projectName", "服务标签");
        model.addAttribute("tableThs", ths);
        model.addAttribute("tableTdCodes", codes);
        model.addAttribute("urlAll", "/api/fwbq/all");
        model.addAttribute("urlDifferent", "/api/fwbq/different");
        model.addAttribute("syncUrl", "/api/fwbq/sync");
        return "admin/compare";
    }

    /**
     * 第三方系统字典
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/dsfxtzd"})
    public String dsfxtzd(Model model) {
        String codes = "idx,xtbs,xtmc,dev,test,testtjd,pro";
        String[] ths = {"#", "系统标识", "系统名称", "开发", "测试", "突击队", "生产"};
        model.addAttribute("projectName", "系统字典");
        model.addAttribute("tableThs", ths);
        model.addAttribute("tableTdCodes", codes);
        model.addAttribute("urlAll", "/api/dsfxtzd/all");
        model.addAttribute("urlDifferent", "/api/dsfxtzd/different");
        model.addAttribute("syncUrl", "/api/dsfxtzd/sync");
        return "admin/compare";
    }

    /**
     * 转发配置
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/zfpz"})
    public String zfpz(Model model) {
        String codes = "idx,path,ms,dev,test,testtjd,pro";
        String[] ths = {"#", "转发方法", "功能描述", "开发", "测试", "突击队", "生产"};
        model.addAttribute("projectName", "转发配置");
        model.addAttribute("tableThs", ths);
        model.addAttribute("tableTdCodes", codes);
        model.addAttribute("urlAll", "/api/zfpz/all");
        model.addAttribute("urlDifferent", "/api/zfpz/different");
        model.addAttribute("syncUrl", "");
        return "admin/compare";
    }

    /**
     * 服务路由
     *
     * @return
     */
    @RequestMapping(value = {"/fwly"})
    public String fwly() {
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
        model.addAttribute("envA", a);
        model.addAttribute("envB", b);
        return "admin/fwlyCompare";
    }

    /**
     * 服务调用情况查看
     */
    @RequestMapping(value = {"/log"})
    public String log() {
        return "admin/log";
    }

    /**
     * 服务调用情况查看
     */
    @RequestMapping(value = {"/times"})
    public String times() {
        return "admin/times";
    }

    /**
     * 服务调用情况查看
     */
    @RequestMapping(value = {"/times/{fwmc}"})
    public String timesByFwmc(Model model, @PathVariable String fwmc) {
        model.addAttribute("fwmc", fwmc);
        return "admin/timesByFwmc";
    }

    /**
     * 医生绑定
     */
    @RequestMapping(value = {"/doctorBinding"})
    public String doctorBinding() {
        return "admin/doctorBinding";
    }
}
