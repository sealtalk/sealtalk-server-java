package com.rcloud.server.sealtalk.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/14
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class GroupBulletinsDTO {

    private Integer id;

    private Integer groupId;

    private Long timestamp;
    //公告内容
    private String content;
}
