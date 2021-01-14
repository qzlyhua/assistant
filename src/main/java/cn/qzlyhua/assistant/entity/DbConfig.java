package cn.qzlyhua.assistant.entity;

import lombok.Data;

/**
 * @author yanghua
 */
@Data
public class DbConfig {
    private Integer id;
    private String url;
    private String user;
    private String pwd;
    private String envType;
    private String dbType;
}
