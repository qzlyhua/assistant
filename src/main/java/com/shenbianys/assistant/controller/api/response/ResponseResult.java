package com.shenbianys.assistant.controller.api.response;

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
    private Integer code;
    /**
     * 返回信息
     */
    private String message;
    /**
     * 数据
     */
    private Object data;
}
