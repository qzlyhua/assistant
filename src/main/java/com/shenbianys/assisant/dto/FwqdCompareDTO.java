package com.shenbianys.assisant.dto;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @author Yang Hua
 */
@Data
public class FwqdCompareDTO {
    private String fwmc;
    private String fwbh;
    private String fwsm;
    private String bbh;
    private String xgsj;
    private String dev;
    private String test;
    private String testtjd;
    private String pro;

    public FwqdCompareDTO(String env, Map<String, Object> map) {
        this.fwmc = getStringFromMap("fwmc", map);
        this.fwbh = getStringFromMap("fwbh", map);
        this.fwsm = getStringFromMap("fwsm", map);
        this.bbh = getStringFromMap("dbbh", map) + "-" + getStringFromMap("bbh", map);
        this.xgsj = getDateStringFromMap("xgsj", map);
        this.dev = "dev".equals(env) ? "1" : "0";
        this.test = "test".equals(env) ? "1" : "0";
        this.testtjd = "testtjd".equals(env) ? "1" : "0";
        this.pro = "pro".equals(env) ? "1" : "0";
    }

    public void setEnv(String env) {
        if ("dev".equals(env)) {
            setDev("1");
        } else if ("test".equals(env)) {
            setTest("1");
        } else if ("testtjd".equals(env)) {
            setTesttjd("1");
        } else if ("pro".equals(env)) {
            setPro("1");
        }
    }

    public boolean isAllSet() {
        return getDev().equals("1") && getTest().equals("1") && getTesttjd().equals("1") && getPro().equals("1");
    }

    private String getStringFromMap(String key, Map<String, Object> map) {
        if (map != null && map.containsKey(key)) {
            return map.get(key).toString();
        } else {
            return "";
        }
    }

    private String getDateStringFromMap(String key, Map<String, Object> map) {
        if (map != null && map.containsKey(key)) {
            Object date = map.get(key);
            if (date != null) {
                return new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date);
            } else {
                return "";
            }
        } else {
            return "";
        }
    }
}
