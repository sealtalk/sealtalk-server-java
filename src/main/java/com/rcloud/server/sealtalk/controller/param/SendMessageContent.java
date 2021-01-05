package com.rcloud.server.sealtalk.controller.param;

import lombok.Data;

/**
 * @Author: Jianlu.Yu
 * @Date: 2021/1/5
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class SendMessageContent {

    private String title;
    private String content;
    private String imageUri;
    private String url;
    private String extra;
}
