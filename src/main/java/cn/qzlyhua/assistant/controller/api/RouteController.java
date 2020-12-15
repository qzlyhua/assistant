package cn.qzlyhua.assistant.controller.api;

import cn.hutool.core.map.MapUtil;
import cn.qzlyhua.assistant.controller.api.response.Response;
import cn.qzlyhua.assistant.dto.EnvInfoDTO;
import cn.qzlyhua.assistant.dto.RouteConfigDetail;
import cn.qzlyhua.assistant.dto.RouteConfigInfo;
import cn.qzlyhua.assistant.service.OriginService;
import cn.qzlyhua.assistant.service.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路由配置
 *
 * @author yanghua
 */
@RestController
@Response
@RequestMapping("/api")
@Slf4j
public class RouteController {
    @Resource
    OriginService originService;

    @Resource
    RouteService routeService;

    /**
     * 各环境各用户域路由配置情况
     *
     * @return
     * @throws SQLException
     */
    @RequestMapping("/routeConfigInfos")
    public List<RouteConfigInfo> routeConfigs() throws SQLException {
        return originService.getOrignsForCompare();
    }

    /**
     * 环境_用户域（A） 与 环境_用户域（B）比对
     *
     * @param a
     * @param b
     * @return 路由名称、环境A配置情况、环境B配置情况
     * @throws SQLException
     */
    @RequestMapping("/routeCompare/{a}/{b}/{type}")
    public Map<String, Object> routeConfigsOfAB(@PathVariable String a, @PathVariable String b, @PathVariable String type) throws SQLException {
        String envA = new EnvInfoDTO(a).getEnv();
        String originCodeA = new EnvInfoDTO(a).getOrigin();
        String envB = new EnvInfoDTO(b).getEnv();
        String originCodeB = new EnvInfoDTO(b).getOrigin();
        log.info("routeCompare：{} - {} VS {} - {}", envA, originCodeA, envB, originCodeB);
        List<RouteConfigDetail> result = originService.getRouteConfigDetailOfAB(envA, originCodeA, envB, originCodeB, type);
        Map<String, Object> resp = new HashMap<>(3);
        resp.put("envA", a);
        resp.put("envB", b);
        resp.put("result", result);
        return resp;
    }

    /**
     * 路由同步：
     */
    @RequestMapping("route/sync/{from}/{to}")
    public Map<String, String> sync(@PathVariable String from, @PathVariable String to, @RequestParam String route) throws SQLException {
        EnvInfoDTO envFrom = new EnvInfoDTO(from);
        EnvInfoDTO envTo = new EnvInfoDTO(to);
        routeService.syncRouteConfig(envFrom.getEnv(), envFrom.getOrigin(), envTo.getEnv(), envTo.getOrigin(), route);
        return MapUtil.of("sync", "OK");
    }
}
