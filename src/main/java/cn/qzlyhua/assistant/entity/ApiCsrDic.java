package cn.qzlyhua.assistant.entity;

import lombok.Data;

@Data
public class ApiCsrDic {
    /**
    * 主键
    */
    private Integer id;

    /**
    * 字典类别
    */
    private String type;

    /**
    * 代码
    */
    private String code;

    /**
    * 含义
    */
    private String name;
}