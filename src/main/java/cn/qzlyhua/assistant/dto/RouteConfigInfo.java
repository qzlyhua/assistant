package cn.qzlyhua.assistant.dto;

import lombok.Data;

/**
 * 路由配置信息（总数）
 *
 * @author yanghua
 */
@Data
public class RouteConfigInfo {
    private String originCode;
    private String originName;
    private String envType;
    private String checkBoxHtml;
    private Integer count;
}
