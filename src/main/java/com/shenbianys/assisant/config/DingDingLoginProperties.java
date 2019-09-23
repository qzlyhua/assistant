package com.shenbianys.assisant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Yang Hua
 */
@Component
@ConfigurationProperties(prefix = "assisant.dingding.login")
@Data
public class DingDingLoginProperties {
    private String appid;
    private String appSecret;
    private String callback;
    private String adminOpenid;
}
