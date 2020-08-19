package com.rcloud.server.sealtalk.controller;

import com.rcloud.server.sealtalk.configuration.SealtalkConfig;
import com.rcloud.server.sealtalk.interceptor.ServerApiParamHolder;
import com.rcloud.server.sealtalk.model.ServerApiParams;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/7
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public abstract class BaseController {

    @Resource
    protected SealtalkConfig sealtalkConfig;

    protected Integer getCurrentUserId(HttpServletRequest request) {
        ServerApiParams serverApiParams = ServerApiParamHolder.get();
        if (serverApiParams != null) {
            return serverApiParams.getCurrentUserId();
        } else {
            return null;
        }
    }

    protected SealtalkConfig getSealtalkConfig() {
        return sealtalkConfig;
    }

    protected ServerApiParams getServerApiParams() {
        return ServerApiParamHolder.get();
    }

}
