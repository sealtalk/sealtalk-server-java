package com.rcloud.server.sealtalk.controller;

import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.manager.FriendShipManager;
import com.rcloud.server.sealtalk.model.response.APIResult;
import com.rcloud.server.sealtalk.model.response.APIResultWrap;
import com.rcloud.server.sealtalk.model.response.dto.ContractInfoDTO;
import com.rcloud.server.sealtalk.model.response.dto.InviteDTO;
import com.rcloud.server.sealtalk.util.JacksonUtil;
import com.rcloud.server.sealtalk.util.MiscUtils;
import com.rcloud.server.sealtalk.util.N3d;
import com.rcloud.server.sealtalk.util.ValidateUtils;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
public class FriendshipController extends BaseController {

    @Resource
    private FriendShipManager friendShipManager;

    @ApiOperation(value = "发起添加好友")
    @RequestMapping(value = "/invite", method = RequestMethod.POST)
    public APIResult<Object> invite(
            @ApiParam(name = "friendId", value = "好友 Id", required = true, type = "String", example = "xxx")
            @RequestParam String friendId,
            @ApiParam(name = "message", value = "message", required = true, type = "String", example = "xxx")
            @RequestParam("message") String message,
            HttpServletRequest request
    ) throws ServiceException {

        message = MiscUtils.xss(message, ValidateUtils.FRIEND_REQUEST_MESSAGE_MAX_LENGTH);
        ValidateUtils.checkInviteMessage(message);
        Integer currentUserId = getCurrentUserId(request);
        InviteDTO inviteResponse = friendShipManager.invite(currentUserId, N3d.decode(friendId), message);
        return APIResultWrap.ok(inviteResponse);
    }


    @ApiOperation(value = "同意添加好友")
    @RequestMapping(value = "/agree", method = RequestMethod.POST)
    public APIResult<Object> agree(
            @ApiParam(name = "friendId", value = "好友 Id", required = true, type = "String", example = "xxx")
            @RequestParam String friendId,
            HttpServletRequest request) throws ServiceException {

        Integer currentUserId = getCurrentUserId(request);
        friendShipManager.agree(currentUserId, N3d.decode(friendId));
        return APIResultWrap.ok("");
    }

    @ApiOperation(value = "忽略好友请求")
    @RequestMapping(value = "/ignore", method = RequestMethod.POST)
    public APIResult<Object> ignore(
            @ApiParam(name = "friendId", value = "好友 Id", required = true, type = "String", example = "xxx")
            @RequestParam String friendId,
            HttpServletRequest request) throws ServiceException {

        Integer currentUserId = getCurrentUserId(request);
        friendShipManager.ignore(currentUserId, N3d.decode(friendId));
        return APIResultWrap.ok("");
    }

    @ApiOperation(value = "删除好友")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResult<Object> delete(
            @ApiParam(name = "friendId", value = "好友 Id", required = true, type = "String", example = "xxx")
            @RequestParam String friendId,
            HttpServletRequest request) throws ServiceException {

        Integer currentUserId = getCurrentUserId(request);
        friendShipManager.delete(currentUserId, N3d.decode(friendId));
        return APIResultWrap.ok("");
    }

    @ApiOperation(value = "设置好友备注名")
    @RequestMapping(value = "/set_display_name", method = RequestMethod.POST)
    public APIResult<Object> setDisplayName(
            @ApiParam(name = "friendId", value = "好友 Id", required = true, type = "String", example = "xxx")
            @RequestParam String friendId,
            @ApiParam(name = "displayName", value = "好友备注名", required = true, type = "String", example = "xxx")
            @RequestParam String displayName,
            HttpServletRequest request) throws ServiceException {

        displayName = MiscUtils.xss(displayName, ValidateUtils.FRIEND_REQUEST_MESSAGE_MAX_LENGTH);
        ValidateUtils.checkDisplayName(displayName);
        Integer currentUserId = getCurrentUserId(request);
        friendShipManager.setDisplayName(currentUserId, N3d.decode(friendId), displayName);
        return APIResultWrap.ok("");
    }

    @ApiOperation(value = "获取好友列表")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public APIResult<Object> friendlist(HttpServletRequest request) throws ServiceException {

        Integer currentUserId = getCurrentUserId(request);

        String result = friendShipManager.getFriendList(currentUserId);

        //TODO
        result = MiscUtils.addUpdateTimeToList(result);
        Object object = MiscUtils.encodeResults(JacksonUtil.getJsonNode(result));
        return APIResultWrap.ok(object);
    }


    @ApiOperation(value = "获取好友信息")
    @RequestMapping(value = "/{friendId}/profile", method = RequestMethod.GET)
    public APIResult<Object> getFriendProfile(
            @ApiParam(name = "friendId", value = "好友 Id", required = true, type = "String", example = "xxx")
            @PathVariable String friendId,
            HttpServletRequest request) throws ServiceException {

        Integer currentUserId = getCurrentUserId(request);

        String result = friendShipManager.getFriendProfile(currentUserId, N3d.decode(friendId));

        //TODO
        result = MiscUtils.addUpdateTimeToList(result);
        Object object = MiscUtils.encodeResults(JacksonUtil.getJsonNode(result), "users.id");
        return APIResultWrap.ok(object);
    }


    @ApiOperation(value = "获取通讯录朋友信息列表")
    @RequestMapping(value = "/get_contacts_info", method = RequestMethod.GET)
    public APIResult<?> getContactsInfo(
            @ApiParam(name = "contacstList", value = "手机号列表", required = true, type = "String[]", example = "xxx")
            @RequestParam String[] contacstList,
            HttpServletRequest request) throws ServiceException {

        if (contacstList == null || contacstList.length == 0) {
            return APIResultWrap.error(ErrorCode.REQUEST_ERROR);
        }

        Integer currentUserId = getCurrentUserId(request);
        List<ContractInfoDTO> contractInfoDTOList = friendShipManager.getContactsInfo(currentUserId, contacstList);
        return APIResultWrap.ok(contractInfoDTOList);
    }

    @ApiOperation(value = "批量删除好友")
    @RequestMapping(value = "/batch_delete", method = RequestMethod.GET)
    public APIResult<?> batchDelete(
            @ApiParam(name = "friendIds", value = "好友ID列表", required = true, type = "String[]", example = "xxx")
            @RequestParam String[] friendIds,
            HttpServletRequest request) throws ServiceException {

        if (friendIds == null || friendIds.length == 0) {
            return APIResultWrap.error(ErrorCode.REQUEST_ERROR);
        }

        Integer currentUserId = getCurrentUserId(request);
        friendShipManager.batchDelete(currentUserId, friendIds);
        return APIResultWrap.ok("");
    }

    /**
     * 设置朋友备注和描述
     * ->不传默认为不进行设置
     * ->传空字符串,将设置为空
     *
     * @param friendId
     * @param displayName
     * @param region
     * @param phone
     * @param description
     * @param imageUri
     * @param request
     * @return
     * @throws ServiceException
     */
    @ApiOperation(value = "设置朋友备注和描述")
    @RequestMapping(value = "/set_friend_description", method = RequestMethod.GET)
    public APIResult<?> setFriendDescription(
            @ApiParam(name = "friendId", value = "好友ID", required = true, type = "String", example = "xxx")
            @RequestParam String friendId,
            @ApiParam(name = "displayName", value = "备注", required = true, type = "String", example = "xxx")
            @RequestParam String displayName,
            @ApiParam(name = "region", value = "国家区号", required = true, type = "String", example = "xxx")
            @RequestParam String region,
            @ApiParam(name = "phone", value = "手机号", required = true, type = "String", example = "xxx")
            @RequestParam String phone,
            @ApiParam(name = "description", value = "更多描述", required = true, type = "String", example = "xxx")
            @RequestParam String description,
            @ApiParam(name = "imageUri", value = "照片地址", required = true, type = "String", example = "xxx")
            @RequestParam String imageUri,
            HttpServletRequest request) throws ServiceException {

        if (StringUtils.isEmpty(friendId)) {
            return APIResultWrap.error(ErrorCode.PARAM_ERROR);
        }

        // region,phone 要么都为空，要么都不为空
        if ((StringUtils.isEmpty(region) && !StringUtils.isEmpty(phone)) ||
                (!StringUtils.isEmpty(region) && StringUtils.isEmpty(phone))) {
            return APIResultWrap.error(ErrorCode.PARAM_ERROR);
        }

        Integer currentUserId = getCurrentUserId(request);

        friendShipManager.setFriendDescription(currentUserId, friendId, displayName, region, phone, description, imageUri);

        return APIResultWrap.ok("");
    }


    @ApiOperation(value = "获取朋友备注和描述")
    @RequestMapping(value = "/get_friend_description", method = RequestMethod.GET)
    public APIResult<?> getFriendDescription(
            @ApiParam(name = "friendId", value = "好友ID", required = true, type = "String", example = "xxx")
            @RequestParam String friendId,
            HttpServletRequest request) throws ServiceException {

        if (StringUtils.isEmpty(friendId)) {
            return APIResultWrap.error(ErrorCode.PARAM_ERROR);
        }

        Integer currentUserId = getCurrentUserId(request);

        friendShipManager.getFriendDescription(currentUserId, friendId);

        return APIResultWrap.ok("");
    }

}
