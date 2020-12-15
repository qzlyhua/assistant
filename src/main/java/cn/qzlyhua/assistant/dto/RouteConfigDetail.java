package cn.qzlyhua.assistant.dto;

import lombok.Data;

/**
 * 路由配置详情：网关路由、后端服务、环境A、环境B
 *
 * @author yanghua
 */
@Data
public class RouteConfigDetail {
    private String route;
    private String application;
    private String service;
    private String origin;

    /**
     * 返回1则代表已经配置
     */
    private String envA;
    private String envB;
}
