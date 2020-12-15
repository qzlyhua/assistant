package cn.qzlyhua.assistant.service;

import java.sql.SQLException;

/**
 * @author yanghua
 */
public interface RouteService {
    void syncRouteConfig(String envFrom, String originFrom, String envTo, String originTo, String route) throws SQLException;
}
