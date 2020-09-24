package com.rcloud.server.sealtalk.constant;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/9/1
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public enum  ConversationType {

    PRIVATE(1,"单聊"),
    GROUP(3,"群聊");

    private Integer code;
    private String desc;

    ConversationType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
