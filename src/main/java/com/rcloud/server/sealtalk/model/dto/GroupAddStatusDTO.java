package com.rcloud.server.sealtalk.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/11
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class GroupAddStatusDTO {

    private String id;
    private List<UserStatusDTO> userStatus;

}
