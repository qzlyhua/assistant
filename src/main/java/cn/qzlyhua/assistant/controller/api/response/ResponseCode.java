package cn.qzlyhua.assistant.controller.api.response;

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
     * 所有无法识别的异常默认的返回状态码
     */
    SERVICE_ERROR(500, "业务处理异常"),

    /**
     * 数据校验错误状态码
     */
    RESOURCES_CHECK_ERROR(400, "数据校验错误"),

    /**
     * 远程接口调用失败
     */
    RPC_ERROR(5001, "远程接口调用失败"),

    /**
     * 数据库配置错误
     */
    DB_CONFIG_ERROR(5002, "数据库配置错误");

    /**
     * 状态码
     */
    private final Integer code;
    /**
     * 返回信息
     */
    private final String message;

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
