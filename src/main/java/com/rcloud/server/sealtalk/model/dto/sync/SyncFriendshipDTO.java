package com.rcloud.server.sealtalk.model.dto.sync;

import lombok.Data;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/9/3
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class SyncFriendshipDTO {
    private String friendId;
    private String displayName;
    private Integer status;
    private Long timestamp;

    private SyncUserDTO user;
}
