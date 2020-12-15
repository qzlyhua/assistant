package cn.qzlyhua.assistant.entity;

import lombok.Data;

/**
 * 用户域信息
 *
 * @author yanghua
 */
@Data
public class Origin {
    private Integer id;
    private String envType;
    private String originCode;
    private String originName;
    private String authCode;
    private String address;
}
