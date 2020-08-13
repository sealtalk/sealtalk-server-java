package com.rcloud.server.sealtalk.model.dto;

import lombok.Data;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/13
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class GroupExitedListDTO {


    private Integer quitUserId;

    private String quitNickname;

    private String quitPortraitUri;

    private Integer quitReason;

    private Long quitTime;

    private Integer operatorId;

    private String operatorName;
}
