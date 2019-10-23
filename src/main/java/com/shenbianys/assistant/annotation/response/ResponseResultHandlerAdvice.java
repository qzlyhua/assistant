package com.shenbianys.assistant.annotation.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一响应体处理器 - 返回code，message，data形式的JSON数据
 *
 * @author Yang Hua
 */
@ControllerAdvice(annotations = StandardResponse.class)
@Slf4j
public class ResponseResultHandlerAdvice implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType,
                                  Class c, ServerHttpRequest request, ServerHttpResponse response) {
        // 如果响应返回的对象为统一响应体，则直接返回body
        if (body instanceof ResponseResult) {
            return body;
        } else {
            ResponseResult responseResult = new ResponseResult(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), body);
            if (MediaType.TEXT_PLAIN.equals(mediaType)) {
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return responseResult.toString();
            }

            return responseResult;
        }
    }
}
