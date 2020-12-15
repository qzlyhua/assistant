package cn.qzlyhua.assistant.service;

import cn.qzlyhua.assistant.dto.RouteConfigDetail;
import cn.qzlyhua.assistant.dto.RouteConfigInfo;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yanghua
 */
public interface RouteService {
    List<RouteConfigInfo> getOrignsForCompare() throws SQLException;

    List<RouteConfigDetail> getRouteConfigDetailOfAB(String envA, String originCodeA, String envB, String originCodeB, String type) throws SQLException;

    void syncRouteConfig(String envFrom, String originFrom, String envTo, String originTo, String route) throws SQLException;
}
