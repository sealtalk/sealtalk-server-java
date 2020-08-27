package com.rcloud.server.sealtalk.rongcloud.message;

import io.rong.messages.BaseMessage;
import io.rong.util.GsonUtil;

import java.util.Map;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/20
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class CustomerGroupApplyMessage extends BaseMessage {


    private String operatorUserId;
    private String operation;
    private Map<String, Object> data;


    private transient static final String TYPE = "ST:GrpApply";

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String toString() {
        return GsonUtil.toJson(this, CustomerGroupApplyMessage.class);
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

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
