package cn.qzlyhua.assistant.service;

import cn.qzlyhua.assistant.dto.RouteConfigDetail;
import cn.qzlyhua.assistant.dto.RouteConfigInfo;
import cn.qzlyhua.assistant.entity.Origin;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yanghua
 */
public interface OriginService {
    List<Origin> getAllOrigins();

    List<RouteConfigInfo> getOrignsForCompare() throws SQLException;

    List<RouteConfigDetail> getRouteConfigDetailOfAB(String envA, String originCodeA, String envB, String originCodeB, String type) throws SQLException;
}
