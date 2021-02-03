package cn.qzlyhua.assistant.dto.specification;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 服务
 *
 * @author yanghua
 */
@Data
@Builder
public class Service {
    private String title;
    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务中文名称
     */
    private String serviceNick;

    /**
     * 功能描述
     */
    private String description;

    /**
     * 接口说明
     */
    private String explain;

    /**
     * 入参
     */
    private List<Parameter> reqParameters;
    private String reqExample;

    /**
     * 出参
     */
    private List<Parameter> resParameters;
    private String resExample;

    public String getTitle() {
        return serviceName + "（" + serviceNick + "）";
    }
}
