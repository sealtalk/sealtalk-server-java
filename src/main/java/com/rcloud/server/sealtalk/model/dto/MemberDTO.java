package com.rcloud.server.sealtalk.model.dto;

import lombok.Data;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/25
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class MemberDTO {

    private String groupNickname;
    private Integer role;
    private String createdAt;
    private Long createdTime;
    private String updatedAt;
    private Long updatedTime;

    private UserDTO user;
}
