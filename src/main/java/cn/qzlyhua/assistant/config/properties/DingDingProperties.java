package cn.qzlyhua.assistant.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author yanghua
 */
@Component
@ConfigurationProperties(prefix = "assisant.dingding.login")
@Data
public class DingDingProperties {
    private String appid;
    private String appSecret;
    private String callback;
    private String adminOpenid;
    private String userFilePath;
}
