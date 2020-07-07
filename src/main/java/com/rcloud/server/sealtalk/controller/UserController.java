package com.rcloud.server.sealtalk.controller;

import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.manager.UserManager;
import com.rcloud.server.sealtalk.model.ServerApiParams;
import com.rcloud.server.sealtalk.model.response.Response;
import com.rcloud.server.sealtalk.model.response.ResultWrap;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Api(tags = "用户相关")
@RestController
@RequestMapping("/user")
@Timed(percentiles = {0.9, 0.95, 0.99})
public class UserController {

    @Resource
    private UserManager userManager;

    @ApiOperation(value = "向手机发送验证码")
    @RequestMapping(value = "/send_code", method = RequestMethod.POST)
    public Response sendCode(
        @ApiParam(name = "region", value = "区号", required = true, type = "String", example = "xxx")
        @RequestParam String region,
        @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "xxx")
        @RequestParam String phone
    ) throws ServiceException {
        userManager.sendCode(region, phone);
        return ResultWrap.ok("");
    }

    @ApiOperation(value = "向手机发送验证码(云片服务)")
    @RequestMapping(value = "/send_code_yp", method = RequestMethod.POST)
    public Response sendCodeYp(
        @ApiParam(name = "region", value = "区号", required = true, type = "String", example = "xxx")
        @RequestParam String region,
        @ApiParam(name = "phone", value = "电话号", required = true, type = "String", example = "xxx")
        @RequestParam String phone,
        @SessionAttribute("serverApiParams") ServerApiParams serverApiParams
    ) throws ServiceException {
        userManager.sendCodeYp(region, phone, serverApiParams);
        return ResultWrap.ok("");
    }
}
