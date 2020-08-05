package com.rcloud.server.sealtalk.constant;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/4
 * @Description: 短信服务商类型
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public enum SmsServiceType {

    RONGCLOUD(1),

    YUNPIAN(2);

    private int code;


    SmsServiceType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
