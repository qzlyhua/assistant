package com.shenbianys.assisant.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author Yang Hua
 */
@Aspect
@Component
@Slf4j
public class ControllerLogAop {
    /**
     * 记录开始请求的时间
     */
    private static ThreadLocal<Long> startTime = new ThreadLocal<>();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("execution(public * com.shenbianys..*Controller..*(..))")
    public void webLog() {
    }

    /**
     * 在处理请求前记录
     *
     * @param joinPoint
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        if (sra == null) {
            return;
        }
        startTime.set(System.currentTimeMillis());
        HttpServletRequest request = sra.getRequest();
        log.info("============================================================");
        log.info("{} : {} [FROM : {}]", request.getMethod(), request.getRequestURL().toString(), request.getRemoteAddr());
        log.info("ARGS :{} ", Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * 处理请求结束日志记录
     *
     * @param response
     */
    @AfterReturning(returning = "response", pointcut = "webLog()")
    public void doAfterReturning(Object response) {
        try {
            log.info("RESPONSE : {}", objectMapper.writeValueAsString(response));
        } catch (IOException e) {
            log.warn("Can't serialize return Object to json string", e);
        }

        log.info("HANDLE_TIME : {} ms", +(System.currentTimeMillis() - startTime.get()));
        startTime.remove();
    }
}
