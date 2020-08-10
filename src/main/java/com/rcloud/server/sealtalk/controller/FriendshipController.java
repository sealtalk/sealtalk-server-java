package com.rcloud.server.sealtalk.controller;

import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.manager.InviteManager;
import com.rcloud.server.sealtalk.model.ServerApiParams;
import com.rcloud.server.sealtalk.model.response.InviteResponse;
import com.rcloud.server.sealtalk.model.response.APIResult;
import com.rcloud.server.sealtalk.model.response.APIResultWrap;
import com.rcloud.server.sealtalk.util.MiscUtils;
import com.rcloud.server.sealtalk.util.ValidateUtils;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * @Author: xiuwei.nie
 * @Author: Jianlu.Yu
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Api(tags = "好友相关")
@RestController
@RequestMapping("/friendship")
@Timed(percentiles = {0.9, 0.95, 0.99})
@Slf4j
public class FriendshipController extends BaseController{

    @Resource
    private InviteManager inviteManager;

    @ApiOperation(value = "发起添加好友")
    @RequestMapping(value = "/invite", method = RequestMethod.POST)
    public APIResult invite(
        @ApiParam(name = "friendId", value = "好友 Id", required = true, type = "int", example = "xxx")
        @RequestParam Integer friendId,
        @ApiParam(name = "message", value = "message", required = true, type = "String", example = "xxx")
        @RequestParam("message") String message,
        HttpServletRequest request,
        HttpSession httpSession
    ) throws ServiceException {

        ValidateUtils.checkInviteMessage(message);
        Integer currentUserId = getCurrentUserId(request);

        InviteResponse inviteResponse =inviteManager.invite(currentUserId, friendId, message);
        return APIResultWrap.ok(inviteResponse);
    }
}
