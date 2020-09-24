package com.rcloud.server.sealtalk.model;

import lombok.Data;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/7
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class RequestUriInfo {

    private String uri;
    private String remoteAddress;
    private String ip;
}
