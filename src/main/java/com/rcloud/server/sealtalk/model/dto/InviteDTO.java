package com.rcloud.server.sealtalk.model.dto;

import lombok.Data;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class InviteDTO {

    private String action;

    private String message;

    public InviteDTO(String action, String message) {
        this.action = action;
        this.message = message;
    }
}
