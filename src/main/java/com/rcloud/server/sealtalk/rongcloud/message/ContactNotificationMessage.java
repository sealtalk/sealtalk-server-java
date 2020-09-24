package com.rcloud.server.sealtalk.rongcloud.message;

import io.rong.messages.BaseMessage;
import io.rong.util.GsonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/9/8
 * @Description: 联系人消息体
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */

public class ContactNotificationMessage extends BaseMessage {
    private String operation = "";
    private Map<String, Object> extra = new HashMap<>();
    private String sourceUserId = "";
    private String targetUserId = "";
    private String message = "";
    private static final transient String TYPE = "ST:ContactNtf";

    public ContactNotificationMessage(String sourceUserId, String targetUserId, String operation, String message, Map<String, Object> extra) {
        this.operation = operation;
        this.extra = extra;
        this.sourceUserId = sourceUserId;
        this.targetUserId = targetUserId;
        this.message = message;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, ContactNotificationMessage.class);
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public String getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(String sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
