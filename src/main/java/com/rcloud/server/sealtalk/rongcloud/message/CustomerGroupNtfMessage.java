package com.rcloud.server.sealtalk.rongcloud.message;

import io.rong.messages.BaseMessage;
import io.rong.util.GsonUtil;

import java.util.Map;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/24
 * @Description:  客户自定义的群通知消息
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class CustomerGroupNtfMessage extends BaseMessage {

    private transient static final String TYPE = "ST:GrpNtf";
    private String operatorUserId;
    private String operation;
    private String clearTime;
    private String message ="";
    private Map<String, Object> messageData;


    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, CustomerGroupNtfMessage.class);
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

    public String getClearTime() {
        return clearTime;
    }

    public void setClearTime(String clearTime) {
        this.clearTime = clearTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getMessageData() {
        return messageData;
    }

    public void setMessageData(Map<String, Object> messageData) {
        this.messageData = messageData;
    }
}
