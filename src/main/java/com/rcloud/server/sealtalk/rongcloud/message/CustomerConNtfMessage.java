package com.rcloud.server.sealtalk.rongcloud.message;

import io.rong.messages.BaseMessage;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/24
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class CustomerConNtfMessage extends BaseMessage {
    private transient static final String TYPE = "ST:ConNtf";


    private String operatorUserId;
    private String operation;

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }

    public String getOperatorUserId() {
        return operatorUserId;
    }

    public void setOperatorUserId(String operatorUserId) {
        this.operatorUserId = operatorUserId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
