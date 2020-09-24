package com.rcloud.server.sealtalk.constant;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/12
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public enum UserAddStatus {

    GROUP_ADDED(1, "已经添加"),
    WAIT_MANAGER(2, "等待管理员同意"),
    WAIT_MEMBER(3, "等待组成员同意");

    private Integer code;
    private String description;

    UserAddStatus(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
