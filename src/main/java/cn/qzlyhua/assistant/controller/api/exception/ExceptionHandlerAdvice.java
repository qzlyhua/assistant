package cn.qzlyhua.assistant.controller.api.exception;

import cn.qzlyhua.assistant.controller.api.response.Response;
import cn.qzlyhua.assistant.controller.api.response.ResponseCode;
import cn.qzlyhua.assistant.controller.api.response.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常处理器
 *
 * @author Yang Hua
 */
@ControllerAdvice(annotations = Response.class)
@ResponseBody
@Slf4j
public class ExceptionHandlerAdvice {
    /**
     * 处理未捕获的 Exception
     *
     * @param e 异常
     * @return 统一响应体
     */
    @ExceptionHandler(Exception.class)
    public ResponseData handleException(Exception e) {
        log.error(e.getMessage(), e);
        return new ResponseData(ResponseCode.SERVICE_ERROR.getCode(), ResponseCode.SERVICE_ERROR.getMessage(), null);
    }

    /**
     * 处理未捕获的 RuntimeException
     *
     * @param e 运行时异常
     * @return 统一响应体
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseData handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return new ResponseData(ResponseCode.SERVICE_ERROR.getCode(), ResponseCode.SERVICE_ERROR.getMessage(), null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseData handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return new ResponseData(ResponseCode.RESOURCES_CHECK_ERROR.getCode(), e.getMessage(), null);
    }

    /**
     * 处理自定义的业务异常
     *
     * @param e 业务异常
     * @return 统一响应体
     */
    @ExceptionHandler(AppException.class)
    public ResponseData handleAppException(AppException e) {
        log.error(e.getMessage(), e);
        ResponseCode code = e.getCode();
        return new ResponseData(code.getCode(), code.getMessage(), null);
    }
}
