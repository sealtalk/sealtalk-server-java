package com.rcloud.server.sealtalk.model.dto;

import lombok.Data;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/10
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class FriendDTO {

    private String displayName;
    private String region;
    private String phone;
    private String description;
    private String imageUri;

}
