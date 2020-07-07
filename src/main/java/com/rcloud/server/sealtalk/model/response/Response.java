package com.rcloud.server.sealtalk.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class Response<T> {

    @ApiModelProperty("返回码")
    protected String code;

    @ApiModelProperty("返回码信息")
    protected String msg;

    protected T data;

    public Response(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Response(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
