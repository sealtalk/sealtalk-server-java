package com.rcloud.server.sealtalk.interceptor;

import com.rcloud.server.sealtalk.model.RequestUriInfo;
import com.rcloud.server.sealtalk.model.ServerApiParams;
import com.rcloud.server.sealtalk.util.N3d;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/19
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Slf4j
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

    public static String getTraceId() {
        ServerApiParams serverApiParams = serverApiParamsThreadLocal.get();
        if (serverApiParams != null) {
            if (serverApiParams != null) {
                return serverApiParams.getTraceId();
            } else {
                return "";
            }
        }
        return "";
    }

    public static String getURI() {
        ServerApiParams serverApiParams = serverApiParamsThreadLocal.get();
        if (serverApiParams != null) {
            RequestUriInfo requestUriInfo = serverApiParams.getRequestUriInfo();
            if (requestUriInfo != null) {
                return serverApiParams.getRequestUriInfo().getUri();
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public static String getEncodedCurrentUserId() {
        ServerApiParams serverApiParams = serverApiParamsThreadLocal.get();
        if (serverApiParams != null) {
            if (serverApiParams.getCurrentUserId() != null) {
                try {
                    return N3d.encode(serverApiParams.getCurrentUserId());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return "";
                }
            }
        }
        return "";
    }
}