package cn.qzlyhua.assistant.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.qzlyhua.assistant.dto.RouteConfigDetail;
import cn.qzlyhua.assistant.dto.RouteConfigInfo;
import cn.qzlyhua.assistant.entity.Origin;
import cn.qzlyhua.assistant.mapper.OriginMapper;
import cn.qzlyhua.assistant.service.DbManager;
import cn.qzlyhua.assistant.service.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.*;

/**
 * 路由配置管理
 *
 * @author yanghua
 */
@Service
@Slf4j
public class RouteServiceImpl implements RouteService {
    @Resource
    DbManager dbManager;

    @Resource
    OriginMapper originMapper;

    @Override
    public void syncRouteConfig(String originFrom, String originTo, String route) throws SQLException {
        log.info("sync：{} - {} -> {}", route, originFrom, originTo);

        // 校验源环境路由配置
        String envFrom = originMapper.getEnvByOriginCode(originFrom);
        RouteConfigDetail routeConfigOfSource = dbManager.getRouteConfigByCRoute(envFrom, originFrom, route);
        if (routeConfigOfSource == null) {
            routeConfigOfSource = dbManager.getRouteConfigByCRoute(envFrom, "0", route);
        }
        Assert.isTrue(routeConfigOfSource != null, "源域未配置该路由！");

        // 校验目标环境是否已经存在该路由配置
        String envTo = originMapper.getEnvByOriginCode(originTo);
        RouteConfigDetail routeConfigOfTarget = dbManager.getRouteConfigByCRoute(envTo, originTo, route);
        if (routeConfigOfTarget == null) {
            routeConfigOfTarget = dbManager.getRouteConfigByCRoute(envTo, "0", route);
        }
        Assert.isTrue(routeConfigOfTarget == null, "目标域已经配置该路由！");

        // 根据源环境复制配置信息至目标用户
        dbManager.insertRouteConfig(originTo, routeConfigOfSource);

        // 刷新路由 https://dev.wiseheartdoctor.com/api/refreshRoute
        String refreshUrl = originMapper.getAddressByOriginCode(originTo);
        HttpUtil.get(refreshUrl);
    }

    /**
     * 获取各环境各用户域的路由配置信息，用于两两比对
     *
     * @return
     * @throws SQLException
     */
    @Override
    public List<RouteConfigInfo> getOrignsForCompare() throws SQLException {
        List<Origin> origins = originMapper.getOriginsForCompare();
        List<RouteConfigInfo> result = new ArrayList<>(origins.size());
        for (Origin origin : origins) {
            RouteConfigInfo r = new RouteConfigInfo();
            BeanUtil.copyProperties(origin, r, false);
            r.setCount(getCount(origin));
            r.setCheckBoxHtml(getCheckBoxHtml(origin));
            result.add(r);
        }
        return result;
    }

    /**
     * 比对指定的两个用户域的路由配置
     *
     * @param originCodeA
     * @param originCodeB
     * @return
     */
    @Override
    public List<RouteConfigDetail> getRouteConfigDetailOfAB(String originCodeA, String originCodeB, String type) throws SQLException {
        List<RouteConfigDetail> configOfA = dbManager.getRouteConfigDetails(originCodeA);
        List<RouteConfigDetail> configOfB = dbManager.getRouteConfigDetails(originCodeB);

        // 两个域的所有已配置网关路由(包括0用户域)
        Set<String> routes = new HashSet<>(Math.max(configOfA.size(), configOfB.size()));

        Map<String, RouteConfigDetail> mapA = new HashMap<>(configOfA.size());
        for (RouteConfigDetail routeConfigDetail : configOfA) {
            routes.add(routeConfigDetail.getRoute());
            mapA.put(routeConfigDetail.getRoute(), routeConfigDetail);
        }

        Map<String, RouteConfigDetail> mapB = new HashMap<>(configOfB.size());
        for (RouteConfigDetail routeConfigDetail : configOfB) {
            routes.add(routeConfigDetail.getRoute());
            mapB.put(routeConfigDetail.getRoute(), routeConfigDetail);
        }

        List<RouteConfigDetail> result = new ArrayList<>(routes.size());
        for (String route : routes) {
            // A与B均已配置
            if (mapA.containsKey(route) && mapB.containsKey(route)) {
                String applicationA = StrUtil.cleanBlank(mapA.get(route).getApplication());
                String serviceA = StrUtil.cleanBlank(mapA.get(route).getService());
                String originA = mapA.get(route).getOrigin();

                String applicationB = StrUtil.cleanBlank(mapB.get(route).getApplication());
                String serviceB = StrUtil.cleanBlank(mapB.get(route).getService());
                String originB = mapB.get(route).getOrigin();

                boolean originLevelSame = (originA.equals("0") && originB.equals("0"))
                        || (!originA.equals("0") && !originB.equals("0"));
                if (originLevelSame && applicationA.equals(applicationB) && serviceA.equals(serviceB)) {
                    if ("all".equals(type)) {
                        RouteConfigDetail routeConfigDetail = new RouteConfigDetail();
                        routeConfigDetail.setRoute(route);
                        routeConfigDetail.setApplication(applicationA);
                        routeConfigDetail.setService(serviceA);
                        routeConfigDetail.setEnvA("1");
                        routeConfigDetail.setEnvB("1");
                        routeConfigDetail.setOrigin(originA);
                        result.add(routeConfigDetail);
                    }
                } else {
                    RouteConfigDetail routeConfigDetail = new RouteConfigDetail();
                    routeConfigDetail.setRoute(route);
                    routeConfigDetail.setOrigin(originA);
                    routeConfigDetail.setApplication(applicationA);
                    routeConfigDetail.setService(serviceA);
                    routeConfigDetail.setEnvA("1");
                    routeConfigDetail.setEnvB("0");

                    RouteConfigDetail routeConfigDetail2 = new RouteConfigDetail();
                    routeConfigDetail2.setRoute(route);
                    routeConfigDetail2.setOrigin(originB);
                    routeConfigDetail2.setApplication(applicationB);
                    routeConfigDetail2.setService(serviceB);
                    routeConfigDetail2.setEnvA("0");
                    routeConfigDetail2.setEnvB("1");

                    result.add(routeConfigDetail);
                    result.add(routeConfigDetail2);
                }
            } else if (mapA.containsKey(route) && !mapB.containsKey(route)) {
                RouteConfigDetail routeConfigDetail = build(mapA.get(route));
                routeConfigDetail.setWarning("1");
                routeConfigDetail.setEnvA("1");
                routeConfigDetail.setEnvB("0");
                result.add(routeConfigDetail);
            } else if (!mapA.containsKey(route) && mapB.containsKey(route)) {
                RouteConfigDetail routeConfigDetail = build(mapB.get(route));
                routeConfigDetail.setWarning("1");
                routeConfigDetail.setEnvA("0");
                routeConfigDetail.setEnvB("1");
                result.add(routeConfigDetail);
            }
        }

        return result;
    }

    private int getCount(Origin origin) throws SQLException {
        return dbManager.selectRouteConfigCount(origin.getOriginCode());
    }

    /**
     * 拼接checkbox的html
     *
     * @param origin
     * @return
     */
    private String getCheckBoxHtml(Origin origin) {
        String code = origin.getOriginCode();
        String cbx = "<input type=\"checkbox\" id=\"" + code + "\" name=\"cbx\"><label for=\"cbx\"></label>";
        return cbx;
    }

    private RouteConfigDetail build(RouteConfigDetail routeConfigDetail) {
        RouteConfigDetail result = new RouteConfigDetail();
        result.setRoute(routeConfigDetail.getRoute());
        result.setOrigin(routeConfigDetail.getOrigin());
        result.setApplication(routeConfigDetail.getApplication());
        result.setService(routeConfigDetail.getService());
        return result;
    }
}
