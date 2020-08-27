package com.rcloud.server.sealtalk.model.dto;

import io.swagger.models.auth.In;
import lombok.Data;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/24
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class UserDTO {

    private String id;
    private String nickname;
    private String region;
    private String phone;
    private String portraitUri;
    private String gender;
    private String stAccount;


}
