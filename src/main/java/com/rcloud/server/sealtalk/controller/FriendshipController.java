package com.rcloud.server.sealtalk.controller;

import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.controller.param.FriendshipParam;
import com.rcloud.server.sealtalk.controller.param.InviteFriendParam;
import com.rcloud.server.sealtalk.domain.Friendships;
import com.rcloud.server.sealtalk.domain.Users;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.manager.FriendShipManager;
import com.rcloud.server.sealtalk.model.dto.*;
import com.rcloud.server.sealtalk.model.response.APIResult;
import com.rcloud.server.sealtalk.model.response.APIResultWrap;
import com.rcloud.server.sealtalk.util.MiscUtils;
import com.rcloud.server.sealtalk.util.N3d;
import com.rcloud.server.sealtalk.util.ValidateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Slf4j
public class FriendshipController extends BaseController {

    @Resource
    private FriendShipManager friendShipManager;

    @ApiOperation(value = "发起添加好友")
    @RequestMapping(value = "/invite", method = RequestMethod.POST)
    public APIResult<Object> invite(@RequestBody InviteFriendParam inviteFriendParam) throws ServiceException {

        if (inviteFriendParam == null) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR);
        }

        String friendId = inviteFriendParam.getFriendId();
        if (StringUtils.isEmpty(friendId)) {
            throw new ServiceException(ErrorCode.PARAMETER_ERROR);
        }

        String message = inviteFriendParam.getMessage();

        message = MiscUtils.xss(message, ValidateUtils.FRIEND_REQUEST_MESSAGE_MAX_LENGTH);
         //去掉邀请好友的提示信息
        ValidateUtils.checkInviteMessage(message);
        Integer currentUserId = getCurrentUserId();
        InviteDTO inviteResponse = friendShipManager.invite(currentUserId, N3d.decode(friendId), message);
        return APIResultWrap.ok(inviteResponse);
    }


    @ApiOperation(value = "同意添加好友")
    @RequestMapping(value = "/agree", method = RequestMethod.POST)
    public APIResult<Object> agree(@RequestBody FriendshipParam friendshipParam) throws ServiceException {

        String friendId = friendshipParam.getFriendId();
        ValidateUtils.notEmpty(friendId);
        log.info("agree friendId:" + friendId);
        Integer currentUserId = getCurrentUserId();
        friendShipManager.agree(currentUserId, N3d.decode(friendId));
        return APIResultWrap.ok();
    }

    @ApiOperation(value = "忽略好友请求")
    @RequestMapping(value = "/ignore", method = RequestMethod.POST)
    public APIResult<Object> ignore(@RequestBody FriendshipParam friendshipParam) throws ServiceException {
        String friendId = friendshipParam.getFriendId();
        ValidateUtils.notEmpty(friendId);

        Integer currentUserId = getCurrentUserId();
        friendShipManager.ignore(currentUserId, N3d.decode(friendId));
        return APIResultWrap.ok();
    }

    @ApiOperation(value = "删除好友")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResult<Object> delete(@RequestBody FriendshipParam friendshipParam) throws ServiceException {

        String friendId = friendshipParam.getFriendId();
        ValidateUtils.notEmpty(friendId);

        Integer currentUserId = getCurrentUserId();
        friendShipManager.delete(currentUserId, N3d.decode(friendId));
        return APIResultWrap.ok();
    }

    @ApiOperation(value = "设置好友备注名")
    @RequestMapping(value = "/set_display_name", method = RequestMethod.POST)
    public APIResult<Object> setDisplayName(@RequestBody FriendshipParam friendshipParam) throws ServiceException {

        String friendId = friendshipParam.getFriendId();
        ValidateUtils.notEmpty(friendId);

        String displayName = friendshipParam.getDisplayName();
        displayName = MiscUtils.xss(displayName, ValidateUtils.FRIEND_REQUEST_MESSAGE_MAX_LENGTH);
        ValidateUtils.checkDisplayName(displayName);
        Integer currentUserId = getCurrentUserId();
        friendShipManager.setDisplayName(currentUserId, N3d.decode(friendId), displayName);
        return APIResultWrap.ok();
    }

    @ApiOperation(value = "获取好友列表")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public APIResult<Object> friendList() throws ServiceException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        Integer currentUserId = getCurrentUserId();

        List<Friendships> friendshipsList = friendShipManager.getFriendList(currentUserId);

        List<FriendShipDTO> friendShipDTOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(friendshipsList)) {
            for (Friendships friendships : friendshipsList) {
                FriendShipDTO dto = new FriendShipDTO();
                dto.setDisplayName(friendships.getDisplayName());
                dto.setMessage(friendships.getMessage());
                dto.setStatus(friendships.getStatus());

                dto.setUpdatedAt(sdf.format(friendships.getUpdatedAt()));
                dto.setUpdatedTime(friendships.getUpdatedAt().getTime());
                UserDTO userDTO = new UserDTO();
                Users users = friendships.getUsers();
                if (users != null) {
                    userDTO.setId(N3d.encode(users.getId()));
                    userDTO.setNickname(users.getNickname());
                    userDTO.setPortraitUri(users.getPortraitUri());
                    userDTO.setRegion(users.getRegion());
                    userDTO.setPhone(users.getPhone());
                    userDTO.setGender(users.getGender());
                    userDTO.setStAccount(users.getStAccount());
                }
                dto.setUser(userDTO);
                friendShipDTOS.add(dto);
            }
        }

        return APIResultWrap.ok(friendShipDTOS);
    }


    @ApiOperation(value = "获取好友信息")
    @RequestMapping(value = "/{friendId}/profile", method = RequestMethod.GET)
    public APIResult<Object> getFriendProfile(
            @ApiParam(name = "friendId", value = "好友 Id", required = true, type = "String", example = "xxx")
            @PathVariable String friendId) throws ServiceException {

        Integer currentUserId = getCurrentUserId();

        Friendships friendships = friendShipManager.getFriendProfile(currentUserId, N3d.decode(friendId));

        Map<String, Object> resultMap = new HashMap<>();

        if (friendships != null) {
            resultMap.put("displayName", friendships.getDisplayName());
            Map<String, Object> userMap = new HashMap<>();
            Users users = friendships.getUsers();
            if (users != null) {
                userMap.put("id", N3d.encode(users.getId()));
                userMap.put("nickname", users.getNickname());
                userMap.put("region", users.getRegion());
                userMap.put("phone", users.getPhone());
                userMap.put("portraitUri", users.getPortraitUri());
            }
            resultMap.put("user", userMap);
        }

        return APIResultWrap.ok(resultMap);
    }


    @ApiOperation(value = "获取通讯录朋友信息列表")
    @RequestMapping(value = "/get_contacts_info", method = RequestMethod.POST)
    public APIResult<?> getContactsInfo(@RequestBody FriendshipParam friendshipParam) throws ServiceException {
        String[] contactList = friendshipParam.getContactList();
        ValidateUtils.notEmpty(contactList);

        Integer currentUserId = getCurrentUserId();
        List<ContractInfoDTO> contractInfoDTOList = friendShipManager.getContactsInfo(currentUserId, contactList);
        return APIResultWrap.ok(contractInfoDTOList);
    }

    @ApiOperation(value = "批量删除好友")
    @RequestMapping(value = "/batch_delete", method = RequestMethod.POST)
    public APIResult<Object> batchDelete(@RequestBody FriendshipParam friendshipParam) throws ServiceException {
        String[] friendIds = friendshipParam.getFriendIds();
        ValidateUtils.notEmpty(friendIds);

        List<Integer> decodeFriendIds = new ArrayList<>();
        for (String friendId : friendIds) {
            decodeFriendIds.add(N3d.decode(friendId));
        }

        Integer currentUserId = getCurrentUserId();

        friendShipManager.batchDelete(currentUserId, decodeFriendIds);
        return APIResultWrap.ok();
    }

    /**
     * 设置朋友备注和描述
     * ->不传默认为不进行设置
     * ->传空字符串,将设置为空
     *
     * @param friendshipParam
     * @return
     * @throws ServiceException
     */
    @ApiOperation(value = "设置朋友备注和描述")
    @RequestMapping(value = "/set_friend_description", method = RequestMethod.POST)
    public APIResult<Object> setFriendDescription(@RequestBody FriendshipParam friendshipParam) throws ServiceException {

        String friendId = friendshipParam.getFriendId();
        String region = friendshipParam.getRegion();
        String phone = friendshipParam.getPhone();
        String displayName = friendshipParam.getDisplayName();
        String description = friendshipParam.getDescription();
        String imageUri = friendshipParam.getImageUri();

        if (StringUtils.isEmpty(friendId)) {
            return APIResultWrap.error(ErrorCode.PARAM_ERROR);
        }

        // region,phone 要么都为空，要么都不为空
        if ((StringUtils.isEmpty(region) && !StringUtils.isEmpty(phone)) ||
                (!StringUtils.isEmpty(region) && StringUtils.isEmpty(phone))) {
            return APIResultWrap.error(ErrorCode.PARAM_ERROR);
        }

        Integer currentUserId = getCurrentUserId();

        friendShipManager.setFriendDescription(currentUserId, N3d.decode(friendId), displayName, region, phone, description, imageUri);

        return APIResultWrap.ok();
    }


    @ApiOperation(value = "获取朋友备注和描述")
    @RequestMapping(value = "/get_friend_description", method = RequestMethod.POST)
    public APIResult<?> getFriendDescription(@RequestBody FriendshipParam friendshipParam) throws ServiceException {
        String friendId = friendshipParam.getFriendId();

        if (StringUtils.isEmpty(friendId)) {
            return APIResultWrap.error(ErrorCode.PARAM_ERROR);
        }

        Integer currentUserId = getCurrentUserId();

        FriendDTO dto = friendShipManager.getFriendDescription(currentUserId, friendId);

        return APIResultWrap.ok(dto);
    }

}
