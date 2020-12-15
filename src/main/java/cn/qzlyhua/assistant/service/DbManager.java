package cn.qzlyhua.assistant.service;

import cn.qzlyhua.assistant.dto.RouteConfigDetail;

import java.sql.SQLException;
import java.util.List;

/**
 * @author yanghua
 */
public interface DbManager {
    /**
     * 统计指定环境用户域的路由配置数量
     *
     * @param env
     * @param originCode
     * @return
     */
    int selectRouteConfigCount(String env, String originCode) throws SQLException;

    /**
     * 查询指定环境用户域的路由配置详情
     */
    List<RouteConfigDetail> getRouteConfigDetails(String env, String originCode) throws SQLException;
}
