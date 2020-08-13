package com.rcloud.server.sealtalk.controller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.domain.GroupExitedLists;
import com.rcloud.server.sealtalk.domain.GroupMembers;
import com.rcloud.server.sealtalk.domain.GroupReceivers;
import com.rcloud.server.sealtalk.domain.Groups;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.manager.GroupManager;
import com.rcloud.server.sealtalk.model.dto.*;
import com.rcloud.server.sealtalk.model.response.APIResult;
import com.rcloud.server.sealtalk.model.response.APIResultWrap;
import com.rcloud.server.sealtalk.util.JacksonUtil;
import com.rcloud.server.sealtalk.util.MiscUtils;
import com.rcloud.server.sealtalk.util.N3d;
import com.rcloud.server.sealtalk.util.ValidateUtils;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
@Api(tags = "群组相关")
@RestController
@RequestMapping("/group")
@Timed(percentiles = {0.9, 0.95, 0.99})
@Slf4j
public class GroupController extends BaseController {

    @Resource
    private GroupManager groupManager;


    @ApiOperation(value = "创建群组")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public APIResult<Object> create(
            @ApiParam(name = "name", value = "群名称", required = true, type = "String", example = "86")
            @RequestParam String name,
            @ApiParam(name = "memberIds", value = "群成员 Id 列表, 包含 创建者 Id", required = true, type = "String", example = "18811111111")
            @RequestParam String[] memberIds,
            @ApiParam(name = "portraitUri", value = "头像地址", required = false, type = "String", example = "18811111111")
            @RequestParam String portraitUri,
            HttpServletRequest request
    ) throws ServiceException {

        name = MiscUtils.xss(name, ValidateUtils.GROUP_NAME_MAX_LENGTH);
        ValidateUtils.checkGroupName(name);
        ValidateUtils.checkMemberIds(memberIds);

        Integer currentUserId = getCurrentUserId(request);

        GroupAddStatusDTO groupAddStatusDTO = groupManager.createGroup(currentUserId, name, memberIds, portraitUri);
        return APIResultWrap.ok(MiscUtils.encodeResults(groupAddStatusDTO));
    }


    @ApiOperation(value = "添加群成员")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public APIResult<Object> addMember(
            @ApiParam(name = "groupId", value = "群组ID", required = true, type = "String", example = "86")
            @RequestParam String groupId,
            @ApiParam(name = "memberIds", value = "群成员 Id 列表", required = true, type = "String", example = "18811111111")
            @RequestParam String[] memberIds,
            HttpServletRequest request
    ) throws ServiceException {

        ValidateUtils.notEmpty(groupId);
        if (ArrayUtils.isEmpty(memberIds)) {
            throw new ServiceException(ErrorCode.GROUP_LIMIT_ERROR);
        }

        Integer currentUserId = getCurrentUserId(request);
        List<UserStatusDTO> userStatusDTOList = groupManager.addMember(currentUserId, groupId, memberIds);

        return APIResultWrap.ok(MiscUtils.encodeResults(userStatusDTOList));
    }

    @ApiOperation(value = "用户加入群组")
    @RequestMapping(value = "/join", method = RequestMethod.POST)
    public APIResult<Object> joinGroup(
            @ApiParam(name = "groupId", value = "群组ID", required = true, type = "String", example = "86")
            @RequestParam String groupId,
            @ApiParam(name = "encodedGroupId", value = "编码群组ID", required = true, type = "String", example = "18811111111")
            @RequestParam String encodedGroupId,
            HttpServletRequest request
    ) throws ServiceException {

        ValidateUtils.notEmpty(groupId);
        ValidateUtils.notEmpty(encodedGroupId);

        Integer currentUserId = getCurrentUserId(request);
        groupManager.joinGroup(currentUserId, Integer.valueOf(groupId), encodedGroupId);

        return APIResultWrap.ok("");
    }
//
//    @ApiOperation(value = "用户加入群组")
//    @RequestMapping(value = "/kickMemberOfGroup", method = RequestMethod.POST)
//    public APIResult<Object> kickMemberOfGroup(
//            @ApiParam(name = "groupId", value = "群组ID", required = true, type = "String", example = "86")
//            @RequestParam String groupId,
//            @ApiParam(name = "memberIds", value = "userId 列表", required = true, type = "String", example = "18811111111")
//            @RequestParam String[] memberIds,
//            HttpServletRequest request
//    ) throws ServiceException {
//
//        ValidateUtils.notEmpty(groupId);
//        ValidateUtils.notNull(memberIds);
//
//        Integer currentUserId = getCurrentUserId(request);
////        groupManager.kickMemberOfGroup(currentUserId, Integer.valueOf(groupId), encodedGroupId);
//
//        return APIResultWrap.ok("");
//
//
//    }

    @ApiOperation(value = "设置自己的群名片")
    @RequestMapping(value = "/set_display_name", method = RequestMethod.POST)
    public APIResult<?> setDisplayName(
            @ApiParam(name = "groupId", value = "群组ID", required = true, type = "String", example = "86")
            @RequestParam String groupId,
            @ApiParam(name = "displayName", value = "群名片", required = true, type = "String", example = "86")
            @RequestParam String displayName,
            HttpServletRequest request) throws ServiceException {

        ValidateUtils.notEmpty(groupId);

        displayName = MiscUtils.xss(displayName, ValidateUtils.GROUP_MEMBER_DISPLAY_NAME_MAX_LENGTH);
        ValidateUtils.checkGroupDisplayName(displayName);

        Integer currentUserId = getCurrentUserId(request);

        groupManager.setDisPlayName(currentUserId,N3d.decode(groupId),displayName);
        return APIResultWrap.ok("");
    }


    @ApiOperation(value = "获取群信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public APIResult<?> getGroupInfo(
            @ApiParam(name = "groupId", value = "群组ID", required = true, type = "String", example = "86")
            @PathVariable("id") String groupId, HttpServletRequest request) throws ServiceException {

        ValidateUtils.notEmpty(groupId);

        Integer currentUserId = getCurrentUserId(request);

        Groups group = groupManager.getGroupInfo(currentUserId,N3d.decode(groupId));
        Object object = MiscUtils.encodeResults(group,"id","creatorId");
        return APIResultWrap.ok(object);
    }

    @ApiOperation(value = "获取群成员列表")
    @RequestMapping(value = "/{id}/members", method = RequestMethod.GET)
    public APIResult<?> getGroupMembers(
            @ApiParam(name = "groupId", value = "群组ID", required = true, type = "String", example = "86")
            @PathVariable("id") String groupId, HttpServletRequest request) throws ServiceException {

        ValidateUtils.notEmpty(groupId);

        Integer currentUserId = getCurrentUserId(request);

        List<GroupMembers> groupMembersList = groupManager.getGroupMembers(currentUserId,N3d.decode(groupId));

        Object object = MiscUtils.encodeResults(groupMembersList,"users.id");
        String result = MiscUtils.addUpdateTimeToList(JacksonUtil.toJson(object));

        //TODO
        return APIResultWrap.ok(JacksonUtil.getJsonNode(result));
    }

    @ApiOperation(value = "设置群认证")
    @RequestMapping(value = "/set_certification", method = RequestMethod.POST)
    public APIResult<?> setCertification(
            @ApiParam(name = "groupId", value = "群组ID", required = true, type = "String", example = "86")
            @RequestParam String groupId,
            @ApiParam(name = "certiStatus", value = "认证状态： 0 开启(需要认证)、1 关闭（不需要认证）", required = true, type = "Integer", example = "86")
            @RequestParam Integer certiStatus,
            HttpServletRequest request
    ) throws ServiceException {

        ValidateUtils.notEmpty(groupId);
        ValidateUtils.inRange(certiStatus,ImmutableList.of(0,1));

        Integer currentUserId = getCurrentUserId(request);

        groupManager.setCertification(currentUserId,Integer.valueOf(groupId),certiStatus);
        return APIResultWrap.ok("");
    }

    @ApiOperation(value = "获取群验证通知消息")
    @RequestMapping(value = "/notice_info", method = RequestMethod.GET)
    public APIResult<?> getNoticeInfo(HttpServletRequest request) throws ServiceException {

        Integer currentUserId = getCurrentUserId(request);

        List<GroupReceivers> groupReceiversList = groupManager.getNoticeInfo(currentUserId);
        List<GroupReceiverDTO> groupReceiverDTOList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(groupReceiversList)){
            for(GroupReceivers groupReceivers:groupReceiversList){
                GroupReceiverDTO dto =new GroupReceiverDTO();
                dto.setId(N3d.encode(groupReceivers.getId()));
                dto.setCreatedTime(groupReceivers.getCreatedAt());
                dto.setStatus(groupReceivers.getStatus());
                dto.setType(groupReceivers.getType());
                Map<String,Object> group = Maps.newHashMap();
                Map<String,Object> receiver = Maps.newHashMap();
                Map<String,Object> requester = Maps.newHashMap();
                if(groupReceivers.getGroup()!=null){
                    group.put("id", N3d.encode(groupReceivers.getGroup().getId()));
                    group.put("name",groupReceivers.getGroup().getName());

                }
                dto.setGroup(group);

                if(groupReceivers.getReceiver()!=null){
                    receiver.put("id",N3d.encode(groupReceivers.getReceiver().getId()));
                    receiver.put("nickname",groupReceivers.getReceiver().getNickname());
                }
                dto.setReceiver(receiver);
                if(groupReceivers.getRequester()!=null){
                    requester.put("id",N3d.encode(groupReceivers.getRequester().getId()));
                    requester.put("nickname",groupReceivers.getRequester().getNickname());
                }
                dto.setRequester(requester);
                groupReceiverDTOList.add(dto);
            }
        }

        return APIResultWrap.ok(groupReceiverDTOList);
    }


    @ApiOperation(value = "清空群验证通知消息")
    @RequestMapping(value = "/clear_notice", method = RequestMethod.POST)
    public APIResult<?> clearNotice(HttpServletRequest request) throws ServiceException {

        Integer currentUserId = getCurrentUserId(request);

        groupManager.clearNotice(currentUserId);

        return APIResultWrap.ok("");
    }


    @ApiOperation(value = "设置/取消 全员禁言")
    @RequestMapping(value = "/mute_all", method = RequestMethod.POST)
    public APIResult<?> setMuteAll(
            @ApiParam(name = "groupId", value = "群组ID", required = true, type = "String", example = "86")
            @RequestParam String groupId,
            @ApiParam(name = "muteStatus", value = "禁言状态：0 关闭 1 开启", required = true, type = "Integer", example = "86")
            @RequestParam Integer muteStatus,
            @ApiParam(name = "userId", value = "可发言用户", required = false, type = "Array[Integer]", example = "86")
            @RequestParam String[] userId,
            HttpServletRequest request
    ) throws ServiceException {

        ValidateUtils.notEmpty(groupId);
        ValidateUtils.notNull(muteStatus);
        ValidateUtils.inRange(muteStatus,ImmutableList.of(0,1));

        Integer currentUserId = getCurrentUserId(request);

        groupManager.setMuteAll(currentUserId,Integer.valueOf(groupId),muteStatus,userId);

        return APIResultWrap.ok("");
    }

    @ApiOperation(value = "设置群定时清理状态")
    @RequestMapping(value = "/set_regular_clear", method = RequestMethod.POST)
    public APIResult<?> setRegularClear(
            @ApiParam(name = "groupId", value = "群组ID", required = true, type = "String", example = "86")
            @RequestParam String groupId,
            @ApiParam(name = "clearStatus", value = "清理选项： 0 关闭、 3 清理 3 天前、 7 清理 7 天前、 36 清理 36 小时前", required = true, type = "Integer", example = "86")
            @RequestParam Integer clearStatus,
            HttpServletRequest request
    ) throws ServiceException {

        ValidateUtils.notEmpty(groupId);
        ValidateUtils.inRange(clearStatus,ImmutableList.of(0,3,7,36));

        Integer currentUserId = getCurrentUserId(request);

        groupManager.setRegularClear(currentUserId,groupId,clearStatus);

        return APIResultWrap.ok("");
    }

    @ApiOperation(value = "获取群定时清理状态")
    @RequestMapping(value = "/get_regular_clear", method = RequestMethod.POST)
    public APIResult<?> getRegularClear(
            @ApiParam(name = "groupId", value = "群组ID", required = true, type = "String", example = "86")
            @RequestParam String groupId,
            HttpServletRequest request
    ) throws ServiceException {

        ValidateUtils.notEmpty(groupId);

        Groups groups = groupManager.getGroup(Integer.valueOf(groupId));

        Integer clearStatus = groups.getClearStatus();
        Map<String, Integer> result = new HashMap<>();
        result.put("clearStatus", clearStatus);
        return APIResultWrap.ok(clearStatus);
    }

    @ApiOperation(value = "设置群成员信息")
    @RequestMapping(value = "/set_member_info", method = RequestMethod.POST)
    public APIResult<?> setMemberInfo(
            @ApiParam(name = "groupId", value = "群组ID", required = true, type = "String", example = "86")
            @RequestParam String groupId,
            @ApiParam(name = "memberId", value = "群用户ID", required = true, type = "String", example = "86")
            @RequestParam String memberId,
            @ApiParam(name = "groupNickname", value = "群成员昵称", required = false, type = "String", example = "86")
            @RequestParam String groupNickname,
            @ApiParam(name = "region", value = "区号", required = false, type = "String", example = "86")
            @RequestParam String region,
            @ApiParam(name = "phone", value = "电话", required = false, type = "String", example = "86")
            @RequestParam String phone,
            @ApiParam(name = "WeChat", value = "微信号", required = false, type = "String", example = "86")
            @RequestParam String WeChat,
            @ApiParam(name = "Alipay", value = "支付宝号", required = false, type = "String", example = "86")
            @RequestParam String Alipay,
            @ApiParam(name = "memberDesc", value = "描述,Array类型", required = false, type = "String", example = "86")
            @RequestParam String[] memberDesc,
            HttpServletRequest request
    ) throws ServiceException {

        ValidateUtils.notEmpty(groupId);
        ValidateUtils.notEmpty(memberId);

        //Integer currentUserId = getCurrentUserId(request); TODO

        groupManager.setMemberInfo(groupId, memberId, groupNickname, region, phone, WeChat, Alipay, memberDesc);
        return APIResultWrap.ok("");
    }


    @ApiOperation(value = "获取群成员信息")
    @RequestMapping(value = "/get_member_info", method = RequestMethod.POST)
    public APIResult<?> getMemberInfo(
            @ApiParam(name = "groupId", value = "群组ID", required = true, type = "String", example = "86")
            @RequestParam String groupId,
            @ApiParam(name = "memberId", value = "群用户ID", required = true, type = "String", example = "86")
            @RequestParam String memberId,
            HttpServletRequest request
    ) throws ServiceException {

        ValidateUtils.notEmpty(groupId);
        ValidateUtils.notEmpty(memberId);

        //Integer currentUserId = getCurrentUserId(request); TODO

        GroupMembers groupMembers = groupManager.getMemberInfo(groupId, memberId);
        GroupMemberDTO dto = new GroupMemberDTO();
        if (groupMembers != null && groupMembers.getMemberDesc() != null) {
            //memberDesc 特殊处理
            dto.setMemberDesc(JacksonUtil.getJsonNode(groupMembers.getMemberDesc()));
        }
        BeanUtils.copyProperties(groupMembers, dto, "memberDesc");

        return APIResultWrap.ok(dto);
    }


    @ApiOperation(value = "获取退群列表")
    @RequestMapping(value = "/exited_list", method = RequestMethod.POST)
    public APIResult<List<GroupExitedListDTO>> getExitedList(
            @ApiParam(name = "groupId", value = "群组ID", required = true, type = "String", example = "86")
            @RequestParam String groupId,
            HttpServletRequest request
    ) throws ServiceException {

        ValidateUtils.notEmpty(groupId);

        Integer currentUserId = getCurrentUserId(request);
        List<GroupExitedListDTO> groupExitedListDTOList = new ArrayList<>();
        List<GroupExitedLists> groupExitedListsList = groupManager.getExitedList(currentUserId, Integer.valueOf(groupId));
        if (!CollectionUtils.isEmpty(groupExitedListsList)) {
            for (GroupExitedLists groupExitedLists : groupExitedListsList) {
                GroupExitedListDTO dto = new GroupExitedListDTO();
                BeanUtils.copyProperties(groupExitedLists, dto);
                groupExitedListDTOList.add(dto);
            }
        }
        return APIResultWrap.ok(groupExitedListDTOList);
    }

    @ApiOperation(value = "设置群成员保护模式")
    @RequestMapping(value = "/set_member_protection", method = RequestMethod.POST)
    public APIResult<Object> setMemberProtection(
            @ApiParam(name = "groupId", value = "群组ID", required = true, type = "String", example = "86")
            @RequestParam String groupId,
            @ApiParam(name = "memberProtection", value = "成员保护模式: 0 关闭、1 开启", required = true, type = "String", example = "18811111111")
            @RequestParam Integer memberProtection,
            HttpServletRequest request
    ) throws ServiceException {

        ValidateUtils.notEmpty(groupId);
        ValidateUtils.inRange(memberProtection, ImmutableList.of(0, 1));

        Integer currentUserId = getCurrentUserId(request);
        groupManager.setMemberProtection(currentUserId, Integer.valueOf(groupId), memberProtection);
        return APIResultWrap.ok("");
    }


}
