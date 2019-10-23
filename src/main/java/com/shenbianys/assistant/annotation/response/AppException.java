package com.shenbianys.assistant.annotation.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义应用异常类
 *
 * @author Yang Hua
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AppException extends RuntimeException {
    private ResponseCode responseCode;

    public AppException(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public AppException(Throwable cause, ResponseCode responseCode) {
        super(cause);
        this.responseCode = responseCode;
    }
}
