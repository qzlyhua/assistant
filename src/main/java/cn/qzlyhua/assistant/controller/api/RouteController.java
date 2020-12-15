package cn.qzlyhua.assistant.controller.api;

import cn.hutool.core.map.MapUtil;
import cn.qzlyhua.assistant.controller.api.response.Response;
import cn.qzlyhua.assistant.dto.RouteConfigDetail;
import cn.qzlyhua.assistant.dto.RouteConfigInfo;
import cn.qzlyhua.assistant.service.OriginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
        String split = "_";
        String envA = a.substring(0, a.indexOf(split));
        String originCodeA = a.substring(a.indexOf(split) + 1);
        String envB = b.substring(0, b.indexOf(split));
        String originCodeB = b.substring(b.indexOf(split) + 1);
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
    @RequestMapping("route/sync/{from}/{to}/{route}")
    public Map<String, String> sync(@PathVariable String from, @PathVariable String to, @PathVariable String route) {
        log.info("sync：{} - {} -> {}", route, from, to);
        return MapUtil.of("sync", "OK");
    }
}
