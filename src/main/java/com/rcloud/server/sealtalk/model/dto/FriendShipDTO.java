package com.rcloud.server.sealtalk.model.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/24
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class FriendShipDTO {

    private String displayName;
    private String message;
    private Integer status;
    private String updatedAt;
    private Long updatedTime;

    private UserDTO user;

}
