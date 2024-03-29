package cn.qzlyhua.assistant.entity;

import lombok.Data;

import java.util.Date;

/**
 * 接口传输规范定义
 */
@Data
public class ApiCsr {
    private Integer id;

    /**
     * 接口路径
     */
    private String path;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 功能描述
     */
    private String description;

    /**
     * 补充说明
     */
    private String remarks;

    /**
     * 入参举例
     */
    private String reqParamsExample;

    /**
     * 出参举例
     */
    private String resParamsExample;

    /**
     * 版本
     */
    private String version;

    /**
     * 业务领域
     */
    private String businessArea;

    /**
     * 二级业务领域
     */
    private String businessSubArea;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;
}