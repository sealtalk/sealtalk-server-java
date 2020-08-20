package com.rcloud.server.sealtalk.configuration;

import com.rcloud.server.sealtalk.interceptor.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final RequestInterceptor requestInterceptor;

    @Resource
    private SealtalkConfig sealtalkConfig;

    @Autowired
    public WebConfiguration(RequestInterceptor requestInterceptor) {
        this.requestInterceptor = requestInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor)
                .addPathPatterns("/friendship/**")
                .addPathPatterns("/user/**")
                .addPathPatterns("/misc/**")
                .addPathPatterns("/group/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(sealtalkConfig.getCorsHosts())
                .allowedMethods("GET,POST,PUT,DELETE,HEAD,OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);

    }
}
