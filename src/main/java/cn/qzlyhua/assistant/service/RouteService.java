package cn.qzlyhua.assistant.service;

import cn.qzlyhua.assistant.dto.RouteConfigDetail;
import cn.qzlyhua.assistant.dto.RouteConfigInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * 路由配置管理
 *
 * @author yanghua
 */
public interface RouteService {
    List<RouteConfigInfo> getOrignsForCompare() throws SQLException;

    List<RouteConfigDetail> getRouteConfigDetailOfAB(String originCodeA, String originCodeB, String type) throws SQLException;

    void syncRouteConfig(String originFrom, String originTo, String route) throws SQLException;
}
