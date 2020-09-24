package com.rcloud.server.sealtalk.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/25
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class GroupDTO {
    private String id;
    private String name;
    private String portraitUri;
    private Integer memberCount;
    private Integer maxMemberCount;
    private String creatorId;
    private String bulletin;
    private Date deletedAt;
    private Integer isMute;
    private Integer certiStatus;
    private Integer memberProtection;

}
