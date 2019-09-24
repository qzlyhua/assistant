package com.shenbianys.assisant.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author Yang Hua
 */
@Data
public class GnsqCompareDTO extends BaseCompareDTO {
    private String id;
    private String dm;
    private String mc;
    private String sjdm;
    private String lx;

    public String getLx() {
        return "1".equals(this.lx) ? "医生端" : "居民端";
    }

    public GnsqCompareDTO(String env, Map<String, Object> map) {
        super(env, map);
        this.id = getStringFromMap("id", map);
        this.dm = getStringFromMap("dm", map);
        this.mc = getStringFromMap("mc", map);
        this.sjdm = getStringFromMap("sjdm", map);
        this.lx = getStringFromMap("lx", map);
    }
}
