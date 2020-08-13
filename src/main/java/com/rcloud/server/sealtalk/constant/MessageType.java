package com.rcloud.server.sealtalk.constant;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/13
 * @Description: 消息类型
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public enum  MessageType {
    GROUP_NOTIFICATION("ST:GrpNtf"),
    CON_NOTIFICATION("ST:ConNtf");




    private String objectName;

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    MessageType(String objectName) {

        this.objectName = objectName;
    }

}
