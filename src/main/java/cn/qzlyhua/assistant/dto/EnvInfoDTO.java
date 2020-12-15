package cn.qzlyhua.assistant.dto;

import lombok.Data;

/**
 * @author yanghua
 */
@Data
public class EnvInfoDTO {
    private String env;
    private String origin;

    public EnvInfoDTO(String s) {
        String split = "_";
        String env = s.substring(0, s.indexOf(split));
        String origin = s.substring(s.indexOf(split) + 1);
        setEnv(env);
        setOrigin(origin);
    }
}
