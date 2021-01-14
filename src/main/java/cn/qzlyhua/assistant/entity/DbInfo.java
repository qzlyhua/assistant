package cn.qzlyhua.assistant.entity;

import lombok.Data;

/**
 * @author yanghua
 */
@Data
public class DbInfo {
    private String sysId;
    private String sysName;
    private String dbType;
    private String dbSchema;
    private String url;
    private String username;
    private String password;
}
