package com.rcloud.server.sealtalk.model.dto;

import lombok.Data;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/11
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class DemoSquareDTO {

    private  int id;
    private String type;
    private String name;
    private String portraitUri;
    private int memberCount;
    private int maxMemberCount;


}
