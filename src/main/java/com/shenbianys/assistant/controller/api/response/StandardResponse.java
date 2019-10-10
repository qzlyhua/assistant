package com.shenbianys.assistant.controller.api.response;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标准响应体注解-使用该注解的方法将返回code、message、data格式的响应数据
 *
 * @author Yang Hua
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface StandardResponse {
}
