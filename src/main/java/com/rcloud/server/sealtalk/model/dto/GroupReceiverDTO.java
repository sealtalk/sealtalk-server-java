package com.rcloud.server.sealtalk.model.dto;

import java.util.Date;
import java.util.Map;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/13
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class GroupReceiverDTO {

    private String id;
    private Integer status;
    private Integer type;
    private Date createdTime;

    private Map<String,Object> group;
    private Map<String,Object> requester;
    private Map<String,Object> receiver;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Map<String, Object> getGroup() {
        return group;
    }

    public void setGroup(Map<String, Object> group) {
        this.group = group;
    }

    public Map<String, Object> getRequester() {
        return requester;
    }

    public void setRequester(Map<String, Object> requester) {
        this.requester = requester;
    }

    public Map<String, Object> getReceiver() {
        return receiver;
    }

    public void setReceiver(Map<String, Object> receiver) {
        this.receiver = receiver;
    }
}
