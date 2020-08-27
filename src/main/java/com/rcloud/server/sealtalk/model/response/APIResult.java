package com.rcloud.server.sealtalk.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class APIResult<T> {

    @ApiModelProperty("返回码")
    protected String code;

    @ApiModelProperty("返回码信息")
    protected String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected T result;

    public APIResult(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public APIResult(String code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }
}
