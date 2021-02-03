package cn.qzlyhua.assistant.dto.specification;

import lombok.Builder;
import lombok.Data;

/**
 * 参数
 *
 * @author yanghua
 */
@Data
@Builder
public class Parameter {
    /**
     * 参数属性名
     */
    private String key;

    /**
     * 参数类型
     */
    private String type;

    /**
     * 参数描述
     */
    private String des;

    /**
     * 参数是否必填
     */
    private String isRequired;
}
