package com.shenbianys.assisant.dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * @author Yang Hua
 */
@Data
public class XtcsCompareDTO extends BaseCompareDTO {
    private String appcode;
    private String csmc;
    private String csms;
    private String xgsj;

    public XtcsCompareDTO(String env, Map<String, Object> map) {
        super(env, map);
        this.appcode = getStringFromMap("appcode", map);
        this.csmc = getStringFromMap("csmc", map);
        this.csms = getStringFromMap("csms", map);
        this.xgsj = getDateStringFromMap("xgsj", map);
    }
}
