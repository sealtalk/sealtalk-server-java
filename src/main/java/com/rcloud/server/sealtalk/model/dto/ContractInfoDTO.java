package com.rcloud.server.sealtalk.model.dto;

import lombok.Data;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/10
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class ContractInfoDTO {

    public static final int REGISTERED = 1;
    public static final int UN_REGISTERED = 0;

    public static final int IS_FRIEND = 1;
    public static final int NON_FRIEND = 0;

    private int registered;     //  0 未注册 1 已注册
    private int relationship;   // 0 非好友 1 好友
    private String stAccount;   // sealtalk 号
    private String phone;       //手机号
    private String id;          //用户ID
    private String nickname;    //昵称
    private String portraitUri;  //头像
}
