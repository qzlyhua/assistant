package cn.qzlyhua.assistant.service.impl;

import cn.qzlyhua.assistant.dto.RouteConfigDetail;
import cn.qzlyhua.assistant.service.DbManager;
import cn.qzlyhua.assistant.service.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.sql.SQLException;

/**
 * @author yanghua
 */
@Service
@Slf4j
public class RouteServiceImpl implements RouteService {
    @Resource
    DbManager dbManager;

    @Override
    public void syncRouteConfig(String envFrom, String originFrom, String envTo, String originTo, String route) throws SQLException {
        log.info("sync：{} - {}[{}] -> {}[{}]", route, envFrom, originFrom, envTo, originTo);
        // 校验目标环境是否已经存在该路由配置
        RouteConfigDetail routeConfigOfTarget = dbManager.getRouteConfigByCRoute(envTo, originTo, route);
        Assert.isTrue(routeConfigOfTarget == null, "目标域已经配置该路由！");

        // 校验源环境路由配置
        RouteConfigDetail routeConfigOfSource = dbManager.getRouteConfigByCRoute(envFrom, originFrom, route);
        Assert.isTrue(routeConfigOfSource != null, "源域未配置该路由！");

        // 根据源环境复制配置信息至目标用户
        dbManager.insertRouteConfig(envTo, originTo, routeConfigOfSource);
    }
}
