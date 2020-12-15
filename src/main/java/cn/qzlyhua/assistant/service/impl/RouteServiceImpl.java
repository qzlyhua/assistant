package cn.qzlyhua.assistant.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
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
     * @param envA
     * @param originCodeA
     * @param envB
     * @param originCodeB
     * @return
     */
    @Override
    public List<RouteConfigDetail> getRouteConfigDetailOfAB(String envA, String originCodeA, String envB, String originCodeB, String type) throws SQLException {
        List<RouteConfigDetail> configOfA = dbManager.getRouteConfigDetails(envA, originCodeA);
        List<RouteConfigDetail> configOfB = dbManager.getRouteConfigDetails(envB, originCodeB);

        // 两个域的所有已配置网管路由
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
            if (mapA.containsKey(route) && mapB.containsKey(route)) {
                String applicationA = StrUtil.cleanBlank(mapA.get(route).getApplication());
                String serviceA = StrUtil.cleanBlank(mapA.get(route).getService());

                String applicationB = StrUtil.cleanBlank(mapB.get(route).getApplication());
                String serviceB = StrUtil.cleanBlank(mapB.get(route).getService());

                if (applicationA.equals(applicationB) && serviceA.equals(serviceB)) {
                    if ("all".equals(type)) {
                        RouteConfigDetail routeConfigDetail = new RouteConfigDetail();
                        routeConfigDetail.setRoute(route);
                        routeConfigDetail.setApplication(applicationA);
                        routeConfigDetail.setService(serviceA);
                        routeConfigDetail.setEnvA("1");
                        routeConfigDetail.setEnvB("1");
                        result.add(routeConfigDetail);
                    }
                } else {
                    RouteConfigDetail routeConfigDetail = new RouteConfigDetail();
                    routeConfigDetail.setRoute(route);
                    routeConfigDetail.setApplication(applicationA);
                    routeConfigDetail.setService(serviceA);
                    routeConfigDetail.setEnvA("1");
                    routeConfigDetail.setEnvB("0");
                    result.add(routeConfigDetail);

                    RouteConfigDetail routeConfigDetail2 = new RouteConfigDetail();
                    routeConfigDetail2.setRoute(route);
                    routeConfigDetail2.setApplication(applicationB);
                    routeConfigDetail2.setService(serviceB);
                    routeConfigDetail2.setEnvA("0");
                    routeConfigDetail2.setEnvB("1");
                    result.add(routeConfigDetail2);
                }
            } else if (mapA.containsKey(route) && !mapB.containsKey(route)) {
                RouteConfigDetail routeConfigDetail = new RouteConfigDetail();
                String applicationA = mapA.get(route).getApplication();
                String serviceA = mapA.get(route).getService();
                routeConfigDetail.setRoute(route);
                routeConfigDetail.setApplication(applicationA);
                routeConfigDetail.setService(serviceA);
                routeConfigDetail.setEnvA("1");
                routeConfigDetail.setEnvB("0");
                result.add(routeConfigDetail);
            } else if (!mapA.containsKey(route) && mapB.containsKey(route)) {
                RouteConfigDetail routeConfigDetail = new RouteConfigDetail();
                String applicationB = mapB.get(route).getApplication();
                String serviceB = mapB.get(route).getService();
                routeConfigDetail.setRoute(route);
                routeConfigDetail.setApplication(applicationB);
                routeConfigDetail.setService(serviceB);
                routeConfigDetail.setEnvA("0");
                routeConfigDetail.setEnvB("1");
                result.add(routeConfigDetail);
            }
        }

        return result;
    }

    private int getCount(Origin origin) throws SQLException {
        String env = origin.getEnvType();
        String code = origin.getOriginCode();
        return dbManager.selectRouteConfigCount(env, code);
    }

    /**
     * 拼接checkbox的html
     *
     * @param origin
     * @return
     */
    private String getCheckBoxHtml(Origin origin) {
        String env = origin.getEnvType();
        String code = origin.getOriginCode();
        String cbx = "<input type=\"checkbox\" id=\"" + env + "_" + code + "\" name=\"cbx\"><label for=\"cbx\"></label>";
        return cbx;
    }
}
