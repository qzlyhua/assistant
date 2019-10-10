package com.shenbianys.assistant.controller.api.response;

/**
 * 响应码 枚举类
 *
 * @author Yang Hua
 */

public enum ResponseCode {
    /**
     * 成功返回的状态码
     */
    SUCCESS(200, "success"),

    /**
     * 数据校验错误状态码
     */
    RESOURCES_CHECK_ERROR(10001, "数据校验错误"),

    /**
     * 所有无法识别的异常默认的返回状态码
     */
    SERVICE_ERROR(500, "业务处理异常"),

    /**
     * 远程接口调用失败
     */
    RPC_ERROR(10500,"远程接口调用失败");

    /**
     * 状态码
     */
    private Integer code;
    /**
     * 返回信息
     */
    private String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
