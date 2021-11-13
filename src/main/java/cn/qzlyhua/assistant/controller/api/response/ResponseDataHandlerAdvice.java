package cn.qzlyhua.assistant.controller.api.response;

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
@ControllerAdvice(annotations = Response.class)
@Slf4j
public class ResponseDataHandlerAdvice implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType,
                                  Class c, ServerHttpRequest request, ServerHttpResponse response) {
        // 如果响应返回的对象为统一响应体，则直接返回body
        if (body instanceof ResponseData) {
            return body;
        } else if (MediaType.APPLICATION_JSON.equals(mediaType) || MediaType.TEXT_PLAIN.equals(mediaType)) {
            ResponseData responseData = new ResponseData(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), body);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return responseData;
        } else {
            // 非JSON格式body直接返回即可
            return body;
        }
    }
}
