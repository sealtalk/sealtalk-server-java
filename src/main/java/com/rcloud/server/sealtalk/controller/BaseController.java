package com.rcloud.server.sealtalk.controller;

import com.rcloud.server.sealtalk.configuration.SealtalkConfig;
import com.rcloud.server.sealtalk.util.AES256;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/7
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public abstract class BaseController {

    @Resource
    private SealtalkConfig sealtalkConfig;

    protected Integer getCurrentUserId(HttpServletRequest request) {
        Integer userId =null;
        Cookie[] cookies = request.getCookies();
        if(cookies!=null && cookies.length>0){
            for(Cookie cookie:cookies){
                if(cookie.getName().equals(sealtalkConfig.getAuthCookieName())){
                    userId = Integer.valueOf(AES256.decrypt(cookie.getValue().getBytes(),sealtalkConfig.getAuthCookieKey()));
                }
            }
        }
        return null;
    }
}
