package cn.qzlyhua.assistant.entity;

import lombok.Data;

@Data
public class ApiCsrParam {
    /**
     * ID
     */
    private Integer id;

    /**
     * 传输规范ID
     */
    private Integer csrId;

    /**
     * 参数类型（入参、出参）
     */
    private String parameterType;

    /**
     * 属性名
     */
    private String key;

    /**
     * 类型
     */
    private String type;

    /**
     * 描述
     */
    private String describe;

    /**
     * 必填说明
     */
    private String required;
}