package com.rcloud.server.sealtalk.constant;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/11
 * @Description: 群组角色
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public enum GroupRole {
    CREATOR(0, "CREATOR"),
    MEMBER(1, "MEMBER"),
    MANAGER(2, "MANAGER");


    private Integer code;
    private String remark;

    GroupRole(Integer code, String remark) {
        this.code = code;
        this.remark = remark;
    }

    public Integer getCode() {
        return code;
    }

    public String getRemark() {
        return remark;
    }
}
