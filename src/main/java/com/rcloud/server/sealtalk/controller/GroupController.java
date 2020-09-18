package com.rcloud.server.sealtalk.controller;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.controller.param.GroupParam;
import com.rcloud.server.sealtalk.controller.param.TransferGroupParam;
import com.rcloud.server.sealtalk.domain.*;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.manager.GroupManager;
import com.rcloud.server.sealtalk.model.dto.*;
import com.rcloud.server.sealtalk.model.response.APIResult;
import com.rcloud.server.sealtalk.model.response.APIResultWrap;
import com.rcloud.server.sealtalk.util.JacksonUtil;
import com.rcloud.server.sealtalk.util.MiscUtils;
import com.rcloud.server.sealtalk.util.N3d;
import com.rcloud.server.sealtalk.util.ValidateUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
@Api(tags = "群组相关")
@RestController
@RequestMapping("/group")
@Slf4j
public class GroupController extends BaseController {

    @Resource
    private GroupManager groupManager;


    @ApiOperation(value = "创建群组")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public APIResult<Object> create(@RequestBody GroupParam groupParam) throws ServiceException {

        String name = groupParam.getName();
        String[] memberIds = groupParam.getMemberIds();
        String portraitUri = groupParam.getPortraitUri();

        ValidateUtils.checkGroupName(name);
        ValidateUtils.notEmpty(memberIds);

        name = MiscUtils.xss(name, ValidateUtils.GROUP_NAME_MAX_LENGTH);
        ValidateUtils.checkGroupName(name);
        ValidateUtils.checkMemberIds(memberIds);
        if (portraitUri == null) {
            portraitUri = "";
        }
        Integer[] decodeMemberIds = MiscUtils.decodeIds(memberIds);

        Integer currentUserId = getCurrentUserId();
        GroupAddStatusDTO groupAddStatusDTO = groupManager.createGroup(currentUserId, name, decodeMemberIds, portraitUri);

        return APIResultWrap.ok(groupAddStatusDTO);
    }


    @ApiOperation(value = "添加群成员")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public APIResult<Object> addMember(@RequestBody GroupParam groupParam) throws ServiceException {
        String groupId = groupParam.getGroupId();
        String[] memberIds = groupParam.getMemberIds();

        ValidateUtils.notEmpty(groupId);
        ValidateUtils.notEmpty(memberIds);

        Integer currentUserId = getCurrentUserId();

        List<UserStatusDTO> userStatusDTOList = groupManager.addMember(currentUserId, N3d.decode(groupId), MiscUtils.decodeIds(memberIds));

        return APIResultWrap.ok(userStatusDTOList);
    }

    @ApiOperation(value = "用户加入群组")
    @RequestMapping(value = "/join", method = RequestMethod.POST)
    public APIResult<Object> joinGroup(@RequestBody GroupParam groupParam) throws ServiceException {

        String groupId = groupParam.getGroupId();

        ValidateUtils.notEmpty(groupId);

        Integer currentUserId = getCurrentUserId();
        groupManager.joinGroup(currentUserId, N3d.decode(groupId), groupId);

        return APIResultWrap.ok();
    }

    @ApiOperation(value = "群主或群管理将群成员移出群组")
    @RequestMapping(value = "/kick", method = RequestMethod.POST)
    public APIResult<Object> kickMember(@RequestBody GroupParam groupParam) throws ServiceException {
        String groupId = groupParam.getGroupId();
        String[] memberIds = groupParam.getMemberIds();

        ValidateUtils.notEmpty(groupId);
        ValidateUtils.notEmpty(memberIds);

        Integer currentUserId = getCurrentUserId();
        groupManager.kickMember(currentUserId, N3d.decode(groupId), groupId, MiscUtils.decodeIds(memberIds), memberIds);

        return APIResultWrap.ok();
    }

    @ApiOperation(value = "退出群组")
    @RequestMapping(value = "/quit", method = RequestMethod.POST)
    public APIResult<?> quitGroup(@RequestBody GroupParam groupParam) throws ServiceException {

        String groupId = groupParam.getGroupId();
        ValidateUtils.notEmpty(groupId);

        Integer currentUserId = getCurrentUserId();

        String resultMessage = groupManager.quitGroup(currentUserId, N3d.decode(groupId), groupId);
        return APIResultWrap.ok(null,resultMessage);
    }


    @ApiOperation(value = "解散群组")
    @RequestMapping(value = "/dismiss", method = RequestMethod.POST)
    public APIResult<Object> dismiss(@RequestBody GroupParam groupParam) throws ServiceException {

        String groupId = groupParam.getGroupId();

        ValidateUtils.notEmpty(groupId);

        Integer currentUserId = getCurrentUserId();

        groupManager.dismiss(currentUserId, N3d.decode(groupId), groupId);
        return APIResultWrap.ok();
    }

    @ApiOperation(value = "转让群主")
    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public APIResult<Object> transfer(@RequestBody TransferGroupParam transferGroupParam) throws ServiceException {

        String groupId = transferGroupParam.getGroupId();
        String userId = transferGroupParam.getUserId();
        ValidateUtils.notEmpty(groupId);
        ValidateUtils.notEmpty(userId);


        Integer currentUserId = getCurrentUserId();

        groupManager.transfer(currentUserId, N3d.decode(groupId), N3d.decode(userId), userId);
        return APIResultWrap.ok();
    }

    @ApiOperation(value = "批量增加管理员")
    @RequestMapping(value = "/set_manager", method = RequestMethod.POST)
    public APIResult<Object> batchSetManager(@RequestBody GroupParam groupParam) throws ServiceException {

        String groupId = groupParam.getGroupId();
        String[] memberIds = groupParam.getMemberIds();
        ValidateUtils.notEmpty(groupId);
        ValidateUtils.notEmpty(memberIds);

        Integer currentUserId = getCurrentUserId();

        groupManager.batchSetManager(currentUserId, N3d.decode(groupId), MiscUtils.decodeIds(memberIds), memberIds);
        return APIResultWrap.ok();
    }

    @ApiOperation(value = "批量删除管理员")
    @RequestMapping(value = "/remove_manager", method = RequestMethod.POST)
    public APIResult<Object> batchRemoveManager(@RequestBody GroupParam groupParam) throws ServiceException {

        String groupId = groupParam.getGroupId();
        String[] memberIds = groupParam.getMemberIds();

        ValidateUtils.notEmpty(groupId);
        ValidateUtils.notEmpty(memberIds);

        Integer currentUserId = getCurrentUserId();

        groupManager.batchRemoveManager(currentUserId, N3d.decode(groupId), MiscUtils.decodeIds(memberIds), memberIds);

        return APIResultWrap.ok();
    }

    @ApiOperation(value = "群组重命名")
    @RequestMapping(value = "/rename", method = RequestMethod.POST)
    public APIResult<Object> rename(@RequestBody GroupParam groupParam) throws ServiceException {

        String name = groupParam.getName();
        String groupId = groupParam.getGroupId();

        name = MiscUtils.xss(name, ValidateUtils.GROUP_NAME_MAX_LENGTH);
        ValidateUtils.checkGroupName(name);

        Integer currentUserId = getCurrentUserId();
        groupManager.rename(currentUserId, N3d.decode(groupId), name, groupId);

        return APIResultWrap.ok();
    }

    @ApiOperation(value = "保存群组至通讯录")
    @RequestMapping(value = "/fav", method = RequestMethod.POST)
    public APIResult<Object> fav(@RequestBody GroupParam groupParam) throws ServiceException {

        String groupId = groupParam.getGroupId();

        if(StringUtils.isEmpty(groupId)){
            throw new ServiceException(ErrorCode.GROUPID_NULL);
        }

        Integer currentUserId = getCurrentUserId();
        groupManager.fav(currentUserId, N3d.decode(groupId));
        return APIResultWrap.ok();
    }

    @ApiOperation(value = "删除群组通讯录")
    @RequestMapping(value = "/fav", method = RequestMethod.DELETE)
    public APIResult<Object> deleteGroupFav(@RequestBody GroupParam groupParam) throws ServiceException {

        String groupId = groupParam.getGroupId();
        ValidateUtils.notEmpty(groupId);

        Integer currentUserId = getCurrentUserId();
        groupManager.deletefav(currentUserId, N3d.decode(groupId));
        return APIResultWrap.ok();
    }

    @ApiOperation(value = "发布群公告")
    @RequestMapping(value = "/set_bulletin", method = RequestMethod.POST)
    public APIResult<Object> setBulletin(@RequestBody GroupParam groupParam) throws ServiceException {

        String groupId = groupParam.getGroupId();

        if(groupId==null || StringUtils.isEmpty(groupId.trim())){
            throw new ServiceException(ErrorCode.GROUPID_NULL);
        }
        String bulletin = groupParam.getBulletin();
        String content = groupParam.getContent();

        bulletin = bulletin == null ? content : bulletin;
        ValidateUtils.notNull(bulletin);

        bulletin = MiscUtils.xss(bulletin, ValidateUtils.GROUP_BULLETIN_MAX_LENGTH);
        ValidateUtils.checkGroupBulletion(bulletin);

        Integer currentUserId = getCurrentUserId();
        groupManager.setBulletin(currentUserId, N3d.decode(groupId), bulletin);

        return APIResultWrap.ok();
    }

    @ApiOperation(value = "获取群公告")
    @RequestMapping(value = "/get_bulletin", method = RequestMethod.GET)
    public APIResult<?> getBulletin(
            @ApiParam(name = "groupId", value = "群组ID", required = true, type = "String", example = "86")
            @RequestParam String groupId) throws ServiceException {

        ValidateUtils.notEmpty(groupId);

        GroupBulletins groupBulletins = groupManager.getBulletin(N3d.decode(groupId));

        GroupBulletinsDTO groupBulletinsDTO = new GroupBulletinsDTO();
        if (groupBulletins == null) {
            throw new ServiceException(ErrorCode.NO_GROUP_BULLETIN);
        } else {
            // 返回给前端的结构id属性需要N3d编码
            groupBulletinsDTO.setGroupId(N3d.encode(groupBulletins.getGroupId()));
            groupBulletinsDTO.setContent(groupBulletins.getContent());
            groupBulletinsDTO.setId(N3d.encode(groupBulletins.getId()));
            groupBulletinsDTO.setTimestamp(groupBulletins.getTimestamp());
        }

        return APIResultWrap.ok(groupBulletinsDTO);
    }

    @ApiOperation(value = "设置群头像")
    @RequestMapping(value = "/set_portrait_uri", method = RequestMethod.POST)
    public APIResult<Object> setGroupPortraitUri(@RequestBody GroupParam groupParam) throws ServiceException {

        String groupId = groupParam.getGroupId();
        String portraitUri = groupParam.getPortraitUri();

        ValidateUtils.notEmpty(groupId);

        ValidateUtils.checkURLFormat(portraitUri);
        portraitUri = MiscUtils.xss(portraitUri, ValidateUtils.PORTRAIT_URI_MAX_LENGTH);
        ValidateUtils.checkGroupPortraitUri(portraitUri);

        Integer currentUserId = getCurrentUserId();

        groupManager.setGroupPortraitUri(currentUserId, N3d.decode(groupId), portraitUri);
        return APIResultWrap.ok("群头像设置成功");
    }


    @ApiOperation(value = "设置自己的群名片")
    @RequestMapping(value = "/set_display_name", method = RequestMethod.POST)
    public APIResult<Object> setDisplayName(@RequestBody GroupParam groupParam) throws ServiceException {

        String groupId = groupParam.getGroupId();
        String displayName = groupParam.getDisplayName();

        ValidateUtils.notEmpty(groupId);

        displayName = MiscUtils.xss(displayName, ValidateUtils.GROUP_MEMBER_DISPLAY_NAME_MAX_LENGTH);
        ValidateUtils.checkGroupDisplayName(displayName);

        Integer currentUserId = getCurrentUserId();

        groupManager.setDisPlayName(currentUserId, N3d.decode(groupId), displayName);
        return APIResultWrap.ok();
    }


    @ApiOperation(value = "获取群信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public APIResult<?> getGroupInfo(
            @ApiParam(name = "groupId", value = "群组ID", required = true, type = "String", example = "86")
            @PathVariable("id") String groupId) throws ServiceException {
        ValidateUtils.notEmpty(groupId);

        Groups group = groupManager.getGroupInfo(N3d.decode(groupId));

        GroupDTO groupDTO = new GroupDTO();
        if (groupDTO != null) {
            groupDTO.setId(N3d.encode(group.getId()));
            groupDTO.setName(group.getName());
            groupDTO.setPortraitUri(group.getPortraitUri());
            groupDTO.setCreatorId(N3d.encode(group.getCreatorId()));
            groupDTO.setMemberCount(group.getMemberCount());
            groupDTO.setMaxMemberCount(group.getMaxMemberCount());
            groupDTO.setCertiStatus(group.getCertiStatus());
            groupDTO.setBulletin(group.getBulletin());
            groupDTO.setIsMute(group.getIsMute());
            groupDTO.setMemberProtection(group.getMemberProtection());
            groupDTO.setDeletedAt(group.getDeletedAt());

        }
        return APIResultWrap.ok(groupDTO);
    }

    @ApiOperation(value = "获取群成员列表")
    @RequestMapping(value = "/{id}/members", method = RequestMethod.GET)
    public APIResult<?> getGroupMembers(
            @ApiParam(name = "groupId", value = "群组ID", required = true, type = "String", example = "86")
            @PathVariable("id") String groupId) throws ServiceException {

        ValidateUtils.notEmpty(groupId);

        Integer currentUserId = getCurrentUserId();

        List<GroupMembers> groupMembersList = groupManager.getGroupMembers(currentUserId, N3d.decode(groupId));

        List<MemberDTO> memberDTOList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMATR_PATTERN);

        if (!CollectionUtils.isEmpty(groupMembersList)) {
            for (GroupMembers groupMembers : groupMembersList) {
                MemberDTO memberDTO = new MemberDTO();
                memberDTO.setGroupNickname(groupMembers.getGroupNickname());
                memberDTO.setRole(groupMembers.getRole());
                memberDTO.setCreatedAt(sdf.format(groupMembers.getCreatedAt()));
                memberDTO.setCreatedTime(groupMembers.getCreatedAt().getTime());
                memberDTO.setUpdatedAt(sdf.format(groupMembers.getUpdatedAt()));
                memberDTO.setUpdatedTime(groupMembers.getUpdatedAt().getTime());

                UserDTO userDTO = new UserDTO();
                memberDTO.setUser(userDTO);

                Users u = groupMembers.getUsers();
                if (u != null) {
                    userDTO.setId(N3d.encode(u.getId()));
                    userDTO.setNickname(u.getNickname());
                    userDTO.setRegion(u.getRegion());
                    userDTO.setPhone(u.getPhone());
                    userDTO.setGender(u.getGender());
                    userDTO.setPortraitUri(u.getPortraitUri());
                    userDTO.setStAccount(u.getStAccount());

                }
                memberDTOList.add(memberDTO);
            }
        }

        return APIResultWrap.ok(memberDTOList);
    }

    @ApiOperation(value = "设置群认证")
    @RequestMapping(value = "/set_certification", method = RequestMethod.POST)
    public APIResult<Object> setCertification(@RequestBody GroupParam groupParam) throws ServiceException {

        String groupId = groupParam.getGroupId();
        Integer certiStatus = groupParam.getCertiStatus();
        ValidateUtils.notEmpty(groupId);
        ValidateUtils.valueOf(certiStatus, ImmutableList.of(Groups.CERTI_STATUS_OPENED, Groups.CERTI_STATUS_CLOSED));

        Integer currentUserId = getCurrentUserId();

        groupManager.setCertification(currentUserId, N3d.decode(groupId), certiStatus);
        return APIResultWrap.ok("");
    }

    @ApiOperation(value = "获取群验证通知消息")
    @RequestMapping(value = "/notice_info", method = RequestMethod.GET)
    public APIResult<?> getNoticeInfo() throws ServiceException {

        Integer currentUserId = getCurrentUserId();

        List<GroupReceivers> groupReceiversList = groupManager.getNoticeInfo(currentUserId);
        List<GroupReceiverDTO> groupReceiverDTOList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMATR_PATTERN);

        if (!CollectionUtils.isEmpty(groupReceiversList)) {
            for (GroupReceivers groupReceivers : groupReceiversList) {
                GroupReceiverDTO dto = new GroupReceiverDTO();
                dto.setId(N3d.encode(groupReceivers.getId()));
                dto.setCreatedTime(groupReceivers.getCreatedAt());
                dto.setCreatedAt(sdf.format(groupReceivers.getCreatedAt()));
                dto.setStatus(groupReceivers.getStatus());
                dto.setType(groupReceivers.getType());
                Map<String, Object> group = Maps.newHashMap();
                Map<String, Object> receiver = Maps.newHashMap();
                Map<String, Object> requester = Maps.newHashMap();
                if (groupReceivers.getGroup() != null) {
                    group.put("id", N3d.encode(groupReceivers.getGroup().getId()));
                    group.put("name", groupReceivers.getGroup().getName());

                }
                dto.setGroup(group);

                if (groupReceivers.getReceiver() != null) {
                    receiver.put("id", N3d.encode(groupReceivers.getReceiver().getId()));
                    receiver.put("nickname", groupReceivers.getReceiver().getNickname());
                }
                dto.setReceiver(receiver);
                if (groupReceivers.getRequester() != null) {
                    requester.put("id", N3d.encode(groupReceivers.getRequester().getId()));
                    requester.put("nickname", groupReceivers.getRequester().getNickname());
                }
                dto.setRequester(requester);
                groupReceiverDTOList.add(dto);
            }
        }

        return APIResultWrap.ok(groupReceiverDTOList);
    }


    @ApiOperation(value = "清空群验证通知消息")
    @RequestMapping(value = "/clear_notice", method = RequestMethod.POST)
    public APIResult<Object> clearNotice() throws ServiceException {

        Integer currentUserId = getCurrentUserId();

        groupManager.clearNotice(currentUserId);

        return APIResultWrap.ok();
    }


    @ApiOperation(value = "设置/取消 全员禁言")
    @RequestMapping(value = "/mute_all", method = RequestMethod.POST)
    public APIResult<Object> setMuteAll(@RequestBody GroupParam groupParam) throws ServiceException {

        String groupId = groupParam.getGroupId();
        Integer muteStatus = groupParam.getMuteStatus();
        String[] userId = groupParam.getUserId();

        ValidateUtils.notEmpty(groupId);
        ValidateUtils.notNull(muteStatus);
        ValidateUtils.valueOf(muteStatus, ImmutableList.of(0, 1));

        Integer currentUserId = getCurrentUserId();

        groupManager.setMuteAll(currentUserId, N3d.decode(groupId), muteStatus, MiscUtils.decodeIds(userId));

        return APIResultWrap.ok("全员禁言成功");
    }

    @ApiOperation(value = "设置群定时清理状态")
    @RequestMapping(value = "/set_regular_clear", method = RequestMethod.POST)
    public APIResult<Object> setRegularClear(@RequestBody GroupParam groupParam) throws ServiceException {

        String groupId = groupParam.getGroupId();
        Integer clearStatus = groupParam.getClearStatus();

        ValidateUtils.notEmpty(groupId);
        //清理选项： 0 关闭、 3 清理 3 天前、 7 清理 7 天前、 36 清理 36 小时前
        ValidateUtils.valueOf(clearStatus, ImmutableList.of(0, 3, 7, 36));

        Integer currentUserId = getCurrentUserId();

        groupManager.setRegularClear(currentUserId, N3d.decode(groupId), clearStatus);

        return APIResultWrap.ok();
    }

    @ApiOperation(value = "获取群定时清理状态")
    @RequestMapping(value = "/get_regular_clear", method = RequestMethod.POST)
    public APIResult<?> getRegularClear(@RequestBody GroupParam groupParam) throws ServiceException {

        String groupId = groupParam.getGroupId();
        ValidateUtils.notEmpty(groupId);

        Groups groups = groupManager.getGroup(N3d.decode(groupId));

        Integer clearStatus = groups.getClearStatus();
        Map<String, Integer> result = new HashMap<>();
        result.put("clearStatus", clearStatus);
        return APIResultWrap.ok(clearStatus);
    }

    @ApiOperation(value = "设置群成员信息")
    @RequestMapping(value = "/set_member_info", method = RequestMethod.POST)
    public APIResult<Object> setMemberInfo(@RequestBody GroupParam groupParam) throws ServiceException {

        String groupId = groupParam.getGroupId();
        String memberId = groupParam.getMemberId();
        String groupNickname = groupParam.getGroupNickname();
        String region = groupParam.getRegion();
        String phone = groupParam.getPhone();
        String WeChat = groupParam.getWeChat();
        String Alipay = groupParam.getAlipay();
        String[] memberDesc = groupParam.getMemberDesc();


        ValidateUtils.notEmpty(groupId);
        ValidateUtils.notEmpty(memberId);


        groupManager.setMemberInfo(N3d.decode(groupId), N3d.decode(memberId), groupNickname, region, phone, WeChat, Alipay, memberDesc);
        return APIResultWrap.ok("设置成功");
    }


    @ApiOperation(value = "获取群成员信息")
    @RequestMapping(value = "/get_member_info", method = RequestMethod.POST)
    public APIResult<?> getMemberInfo(@RequestBody GroupParam groupParam) throws ServiceException {

        String groupId = groupParam.getGroupId();
        String memberId = groupParam.getMemberId();

        ValidateUtils.notEmpty(groupId);
        ValidateUtils.notEmpty(memberId);

        GroupMembers groupMembers = groupManager.getMemberInfo(N3d.decode(groupId), N3d.decode(memberId));

        Map<String, Object> resultMap = new HashMap<>();

        if (groupMembers != null) {
            resultMap.put("isDeleted", groupMembers.getIsDeleted());
            resultMap.put("groupNickname", groupMembers.getGroupNickname());
            resultMap.put("region", groupMembers.getRegion());
            resultMap.put("phone", groupMembers.getPhone());
            resultMap.put("WeChat", groupMembers.getWeChat());
            resultMap.put("Alipay", groupMembers.getAlipay());
            if (groupMembers != null && groupMembers.getMemberDesc() != null) {
                //memberDesc 特殊处理
                resultMap.put("memberDesc", JacksonUtil.getJsonNode(groupMembers.getMemberDesc()));
            } else {
                resultMap.put("memberDesc", null);
            }
        } else {
            resultMap.put("isDeleted", null);
            resultMap.put("groupNickname", null);
            resultMap.put("region", null);
            resultMap.put("phone", null);
            resultMap.put("WeChat", null);
            resultMap.put("Alipay", null);
            resultMap.put("memberDesc", null);
        }

        return APIResultWrap.ok(resultMap);
    }


    @ApiOperation(value = "获取退群列表")
    @RequestMapping(value = "/exited_list", method = RequestMethod.POST)
    public APIResult<?> getExitedList(@RequestBody GroupParam groupParam) throws ServiceException {

        String groupId = groupParam.getGroupId();
        ValidateUtils.notEmpty(groupId);

        Integer currentUserId = getCurrentUserId();
        List<GroupExitedListDTO> groupExitedListDTOList = new ArrayList<>();
        List<GroupExitedLists> groupExitedListsList = groupManager.getExitedList(currentUserId, N3d.decode(groupId));
        if (!CollectionUtils.isEmpty(groupExitedListsList)) {
            for (GroupExitedLists groupExitedLists : groupExitedListsList) {
                GroupExitedListDTO dto = new GroupExitedListDTO();
                BeanUtils.copyProperties(groupExitedLists, dto);
                groupExitedListDTOList.add(dto);
            }
        }
        return APIResultWrap.ok(MiscUtils.encodeResults(groupExitedListDTOList,"quitUserId","operatorId"));
    }

    @ApiOperation(value = "设置群成员保护模式")
    @RequestMapping(value = "/set_member_protection", method = RequestMethod.POST)
    public APIResult<Object> setMemberProtection(@RequestBody GroupParam groupParam) throws ServiceException {

        String groupId = groupParam.getGroupId();
        Integer memberProtection = groupParam.getMemberProtection();
        ValidateUtils.notEmpty(groupId);
        if(memberProtection==null){
            throw new ServiceException(ErrorCode.MemberProtection_NULL);
        }
        ValidateUtils.valueOf(memberProtection, ImmutableList.of(0, 1));

        Integer currentUserId = getCurrentUserId();
        groupManager.setMemberProtection(currentUserId, N3d.decode(groupId), memberProtection);
        return APIResultWrap.ok();

    }


    @ApiOperation(value = "复制群组")
    @RequestMapping(value = "/copy_group", method = RequestMethod.POST)
    public APIResult<GroupAddStatusDTO> copyGroup(@RequestBody GroupParam groupParam) throws ServiceException {
        String groupId = groupParam.getGroupId();
        String name = groupParam.getName();
        String portraitUri = groupParam.getPortraitUri();

        name = MiscUtils.xss(name, ValidateUtils.GROUP_NAME_MAX_LENGTH);
        ValidateUtils.notEmpty(groupId);
        ValidateUtils.checkGroupName(name);
        portraitUri = MiscUtils.xss(portraitUri, ValidateUtils.PORTRAIT_URI_MAX_LENGTH);

        Integer currentUserId = getCurrentUserId();
        GroupAddStatusDTO groupAddStatusDTO = groupManager.copyGroup(currentUserId, N3d.decode(groupId), name, portraitUri);
        return APIResultWrap.ok(groupAddStatusDTO);

    }


    @ApiOperation(value = "同意群邀请")
    @RequestMapping(value = "/agree", method = RequestMethod.POST)
    public APIResult<GroupAddStatusDTO> agree(@RequestBody GroupParam groupParam) throws ServiceException {
        String groupId = groupParam.getGroupId();
        String receiverId = groupParam.getReceiverId();
        String status = groupParam.getStatus();

        ValidateUtils.notEmpty(groupId);
        ValidateUtils.notEmpty(receiverId);
        ValidateUtils.valueOf(status, ImmutableList.of("0", "1"));

        Integer currentUserId = getCurrentUserId();
        groupManager.agree(currentUserId, N3d.decode(groupId), N3d.decode(receiverId), status);
        return APIResultWrap.ok();

    }


}
