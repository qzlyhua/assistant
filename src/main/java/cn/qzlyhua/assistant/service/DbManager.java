package cn.qzlyhua.assistant.service;

import cn.qzlyhua.assistant.dto.RouteConfigDetail;

import java.sql.SQLException;
import java.util.List;

/**
 * 数据库操作管理类
 *
 * @author yanghua
 */
public interface DbManager {
    /**
     * 统计指定环境用户域的路由配置数量
     *
     * @param originCode
     * @return
     */
    int selectRouteConfigCount(String originCode) throws SQLException;

    /**
     * 查询指定环境用户域的路由配置详情
     */
    List<RouteConfigDetail> getRouteConfigDetails(String originCode) throws SQLException;

    /**
     * 根据网关路由查询指定环境≈用户域的路由配置
     */
    RouteConfigDetail getRouteConfigByCRoute(String originCode, String route) throws SQLException;

    int insertRouteConfig(String originCode, RouteConfigDetail routeConfigDetail) throws SQLException;
}
