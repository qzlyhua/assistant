package cn.qzlyhua.assistant.entity;

import lombok.Data;

/**
 * 系统用户
 *
 * @author yanghua
 */
@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String type;
}
