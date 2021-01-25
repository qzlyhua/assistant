package cn.qzlyhua.assistant.controller.web;

import cn.qzlyhua.assistant.config.properties.DingDingProperties;
import cn.qzlyhua.assistant.config.security.SecurityConfig;
import cn.qzlyhua.assistant.config.security.dingding.DingLoginAuthenticationProvider;
import cn.qzlyhua.assistant.util.DingDingUtils;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Set;

/**
 * WEB 页面 MVC Controller
 *
 * @author Yang Hua
 */
@Controller
public class PageController {
    final DingDingProperties dingDingProperties;

    public PageController(DingDingProperties dingDingProperties) {
        this.dingDingProperties = dingDingProperties;
    }

    /**
     * 增加角色标识
     *
     * @param model
     */
    @ModelAttribute
    public void addRoleInfo(Model model) {
        Set<String> roles = AuthorityUtils.authorityListToSet(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        boolean isAdmin = roles.contains(SecurityConfig.ROLE_ADMIN);
        model.addAttribute("isAdmin", isAdmin);

        if (isAdmin) {
            String state = DingLoginAuthenticationProvider.ADD_PREFIX;
            String url = DingDingUtils.getUrl(dingDingProperties, state);
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
     * 服务路由
     *
     * @return
     */
    @RequestMapping(value = {"/route"})
    public String route() {
        return "admin/fwly";
    }

    /**
     * 服务路由比较
     * route/dev_0/dev_001001007011
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"/route/{a}/{b}"})
    public String routeCompare(Model model, @PathVariable String a, @PathVariable String b) {
        model.addAttribute("envA", a);
        model.addAttribute("envB", b);
        return "admin/fwlyCompare";
    }

    /**
     * 数据库表
     *
     * @return
     */
    @RequestMapping(value = {"/db-doc"})
    public String dbDoc() {
        return "admin/db-doc";
    }

    /**
     * 数据库表对比
     *
     * @return
     */
    @RequestMapping(value = {"/db-doc/compare/{a}/{b}"})
    public String dbDocCompare(Model model, @PathVariable String a, @PathVariable String b) {
        model.addAttribute("db1", a);
        model.addAttribute("db2", b);
        return "admin/db-compare";
    }


}
