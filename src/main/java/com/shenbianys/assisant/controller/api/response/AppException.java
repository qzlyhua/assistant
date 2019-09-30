package com.shenbianys.assisant.controller.api.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用异常类
 *
 * @author Yang Hua
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AppException extends RuntimeException {
    private ResponseCode code;

    public AppException(ResponseCode code) {
        this.code = code;
    }

    public AppException(Throwable cause, ResponseCode code) {
        super(cause);
        this.code = code;
    }
}
