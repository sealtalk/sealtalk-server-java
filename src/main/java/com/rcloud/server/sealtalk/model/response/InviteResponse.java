package com.rcloud.server.sealtalk.model.response;

import lombok.Data;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class InviteResponse {

    private String action;

    private String message;

    public InviteResponse(String action, String message) {
        this.action = action;
        this.message = message;
    }
}
