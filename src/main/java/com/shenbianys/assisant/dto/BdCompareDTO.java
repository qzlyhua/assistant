package com.shenbianys.assisant.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author Yang Hua
 */
@Data
public class BdCompareDTO extends BaseCompareDTO {
    private String bdbh;
    private String bdmc;
    private String yylx;


    public BdCompareDTO(String env, Map<String, Object> map) {
        super(env, map);
        this.bdbh = getStringFromMap("bdbh", map);
        this.bdmc = getStringFromMap("bdmc", map);
        this.yylx = getStringFromMap("yylx", map);
    }
}
