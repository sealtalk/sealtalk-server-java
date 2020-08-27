package com.rcloud.server.sealtalk.advice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcloud.server.sealtalk.interceptor.ServerApiParamHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/24
 * @Description: 全局打印 Controller 入参日志
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Aspect
@Configuration
@Slf4j
public class LogAspect {

    private final static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }


    // 定义切点Pointcut
    @Pointcut("execution(* com.rcloud.server.sealtalk.controller..*Controller.*(..))")
    public void executeService() {
    }

    @Before("executeService()")
    public void doBefore(JoinPoint joinPoint) {

        try {
            String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();

            Object[] args = joinPoint.getArgs();
            String[] paramsName = ((MethodSignature) joinPoint.getSignature()).getParameterNames();

            Map<String, Object> paramMap = new HashMap<>();
            if (args != null && paramsName != null && args.length > 0 && paramsName.length > 0) {
                for (int i = 0; i < paramsName.length; i++) {
                    String paramName = paramsName[i];
                    Object paramVal = args[i];
                    if (!(paramVal instanceof HttpServletResponse) && !(paramVal instanceof HttpServletRequest)) {
                        paramMap.put(paramName, args[i]);
                    }
                }
            }

            String uri = ServerApiParamHolder.getURI();
            String traceId = ServerApiParamHolder.getTraceId();
            String uid = ServerApiParamHolder.getEncodedCurrentUserId();
            log.info("invoke controller info: traceId={},uri={},target={},params=[{}],uid={}", traceId, uri, target, objectMapper.writeValueAsString(paramMap), uid);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }


}
