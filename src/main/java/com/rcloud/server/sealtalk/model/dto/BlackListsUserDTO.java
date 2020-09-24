package com.rcloud.server.sealtalk.model.dto;

import lombok.Data;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/31
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class BlackListsUserDTO {

    private String id;
    private String nickname;
    private String portraitUri;
    private String gender;
    private String stAccount;
    private String phone;
    private String updatedAt;
    private Long updatedTime;


}
