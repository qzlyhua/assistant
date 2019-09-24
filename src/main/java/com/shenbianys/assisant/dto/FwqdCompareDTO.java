package com.shenbianys.assisant.dto;

import lombok.Data;

import java.util.Map;

/**
 * @author Yang Hua
 */
@Data
public class FwqdCompareDTO extends BaseCompareDTO {
    private String fwmc;
    private String fwbh;
    private String fwsm;
    private String bbh;
    private String xgsj;

    public FwqdCompareDTO(String env, Map<String, Object> map) {
        super(env, map);
        this.fwmc = getStringFromMap("fwmc", map);
        this.fwbh = getStringFromMap("fwbh", map);
        this.fwsm = getStringFromMap("fwsm", map);
        this.bbh = getStringFromMap("dbbh", map) + "-" + getStringFromMap("bbh", map);
        this.xgsj = getDateStringFromMap("xgsj", map);
    }
}
