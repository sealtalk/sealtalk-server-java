package com.rcloud.server.sealtalk.constant;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/17
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public enum HttpStatusCode {
    CODE_200(200);

    Integer code;

    HttpStatusCode(Integer code) {

        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
