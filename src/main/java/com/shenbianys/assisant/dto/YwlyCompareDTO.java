package com.shenbianys.assisant.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author Yang Hua
 */
@Data
public class YwlyCompareDTO extends BaseCompareDTO {
    private String yylx;
    private String ywlyjb;
    private String ywlymc;

    public YwlyCompareDTO(String env, Map<String, Object> map) {
        super(env, map);
        this.yylx = getStringFromMap("yylx", map);
        this.ywlyjb = getStringFromMap("ywlyjb", map);
        this.ywlymc = getStringFromMap("ywlymc", map);
    }
}
