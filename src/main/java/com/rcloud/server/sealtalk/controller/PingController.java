package com.rcloud.server.sealtalk.controller;

import com.rcloud.server.sealtalk.model.response.APIResult;
import com.rcloud.server.sealtalk.model.response.APIResultWrap;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/20
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@RestController
@RequestMapping("/")
public class PingController {

    @ApiOperation(value = "Ping")
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public APIResult<Object> ping() {
        return APIResultWrap.ok();
    }
}
