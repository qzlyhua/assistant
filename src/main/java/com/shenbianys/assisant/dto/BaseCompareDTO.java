package com.shenbianys.assisant.dto;

import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * @author Yang Hua
 */
@Data
public abstract class BaseCompareDTO {
    protected String dev;
    protected String test;
    protected String testtjd;
    protected String pro;

    public BaseCompareDTO(String env, Map<String, Object> map) {
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
        return "1".equals(getDev()) && "1".equals(getTest()) && "1".equals(getTesttjd()) && "1".equals(getPro());
    }

    protected String getDateStringFromMap(String key, Map<String, Object> map) {
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

    protected String getStringFromMap(String key, Map<String, Object> map) {
        if (map != null && map.containsKey(key)) {
            return map.get(key).toString();
        } else {
            return "";
        }
    }
}
