package com.shenbianys.assisant.controller.api.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一响应体处理器
 *
 * @author Yang Hua
 */
@ControllerAdvice(annotations = StandardResponse.class)
@Slf4j
public class ResponseResultHandlerAdvice implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        log.info("returnType : " + returnType);
        log.info("converterType : " + converterType);
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 判断响应的Content-Type为JSON格式的body，则进行统一处理
        if (MediaType.APPLICATION_JSON.equals(selectedContentType) || MediaType.APPLICATION_JSON_UTF8.equals(selectedContentType)) {
            // 如果响应返回的对象为统一响应体，则直接返回body
            if (body instanceof ResponseResult) {
                return body;
            } else {
                // 只有正常返回的结果才会进入这个判断流程，所以返回正常成功的状态码
                return new ResponseResult(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), body);
            }
        }
        // 非JSON格式body直接返回即可
        return body;
    }
}
