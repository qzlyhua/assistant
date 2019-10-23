package com.shenbianys.assistant.annotation.response;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 统一响应体
 *
 * @author Yang Hua
 */
@Data
@AllArgsConstructor
public class ResponseResult {
    /**
     * 返回状态码
     */
    private String code;
    /**
     * 返回信息
     */
    private String message;
    /**
     * 数据
     */
    private Object data;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
