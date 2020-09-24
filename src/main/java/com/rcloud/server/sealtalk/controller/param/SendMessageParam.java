package com.rcloud.server.sealtalk.controller.param;

import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/24
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class SendMessageParam {

    private String conversationType;
    private String targetId;
    private String objectName;
    private String content;
    private String pushContent;
}
