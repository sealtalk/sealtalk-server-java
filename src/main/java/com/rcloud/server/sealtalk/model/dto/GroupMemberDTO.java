package com.rcloud.server.sealtalk.model.dto;

import lombok.Data;
import org.omg.CORBA.OBJ_ADAPTER;

import javax.persistence.Column;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/13
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class GroupMemberDTO {

    private Boolean isDeleted;
    private String groupNickname;
    private String region;
    private String phone;
    private String WeChat;
    private String Alipay;
    //memberDesc 返回给前端是json对象格式，不是String
    private Object memberDesc;

}
