package com.shenbianys.assistant.annotation.response;

/**
 * 响应码 枚举类
 *
 * @author Yang Hua
 */

public enum ResponseCode {
    /**
     * 成功返回的状态码
     */
    SUCCESS("0", "调用成功"),

    /**
     * 所有无法识别的异常默认的返回状态码
     */
    SERVICE_ERROR("1", "系统内部业务处理异常"),

    /**
     * 远程接口调用失败
     */
    RPC_ERROR("1", "远程接口调用失败"),

    /**
     * 断言错误
     */
    ASSERT_ERROR("1", "Assertion failed");

    /**
     * 继续添加预知的异常类型
     */
    // 继续添加预知的异常类型，注意标点符号

    /**
     * 状态码
     */
    private String code;

    /**
     * 返回信息
     */
    private String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
