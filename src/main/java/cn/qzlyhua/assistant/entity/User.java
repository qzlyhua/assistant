package cn.qzlyhua.assistant.entity;

import lombok.Builder;
import lombok.Data;

/**
 * 系统用户
 *
 * @author yanghua
 */
@Data
@Builder
public class User {
    private Integer id;
    private String nick;
    private String username;
    private String password;
    private String type;
}
