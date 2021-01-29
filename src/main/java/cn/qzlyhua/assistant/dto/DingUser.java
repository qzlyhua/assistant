package cn.qzlyhua.assistant.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author yanghua
 */
@Data
@Builder
public class DingUser {
    private String nick;
    private String openid;
}
