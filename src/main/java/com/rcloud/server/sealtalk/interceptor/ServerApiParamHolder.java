package com.rcloud.server.sealtalk.interceptor;

import com.rcloud.server.sealtalk.model.ServerApiParams;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/19
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class ServerApiParamHolder {

    private static ThreadLocal<ServerApiParams> serverApiParamsThreadLocal = new ThreadLocal<>();

    public static ServerApiParams get() {
        return serverApiParamsThreadLocal.get();
    }

    public static void put(ServerApiParams serverApiParams) {
        serverApiParamsThreadLocal.set(serverApiParams);
    }

    public static void remove() {
        serverApiParamsThreadLocal.remove();
    }
}
