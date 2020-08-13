package com.rcloud.server.sealtalk.manager;

import com.google.common.collect.ImmutableList;
import com.rcloud.server.sealtalk.constant.*;
import com.rcloud.server.sealtalk.domain.*;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.model.dto.GroupAddStatusDTO;
import com.rcloud.server.sealtalk.model.dto.UserStatusDTO;
import com.rcloud.server.sealtalk.rongcloud.RongCloudClient;
import com.rcloud.server.sealtalk.service.*;
import com.rcloud.server.sealtalk.util.CacheUtil;
import com.rcloud.server.sealtalk.util.JacksonUtil;
import com.rcloud.server.sealtalk.util.MiscUtils;
import com.rcloud.server.sealtalk.util.N3d;
import io.rong.messages.GroupNotificationMessage;
import io.rong.models.message.GroupMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.rcloud.server.sealtalk.util.N3d.encode;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/11
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
@Slf4j
public class GroupManager extends BaseManager {

    @Resource
    private RongCloudClient rongCloudClient;

    @Resource
    private GroupsService groupsService;

    @Resource
    private GroupMembersService groupMembersService;

    @Resource
    private UsersService usersService;

    @Resource
    private DataVersionsService dataVersionsService;

    @Resource
    private GroupSyncsService groupSyncsService;

    @Resource
    private GroupReceiversService groupReceiversService;

    @Resource
    private GroupExitedListsService groupExitedListsService;


    /**
     * 根据群Id批量获取群列表
     *
     * @param groupIds
     * @return
     */
    public List<Groups> getGroupList(List<Integer> groupIds) {

        Example example = new Example(Groups.class);
        example.createCriteria().andIn("id", groupIds);
        return groupsService.getByExample(example);
    }

    /**
     * 根据群Id获取群信息
     *
     * @param groupId
     * @return
     */
    public Groups getGroup(Integer groupId) {
        return groupsService.getByPrimaryKey(groupId);
    }

    //TODO 重点测试
    //TODO 重点测试
    public GroupAddStatusDTO createGroup(Integer currentUserId, String name, String[] memberIds, String portraitUri) throws ServiceException {

        List<UserStatusDTO> userStatusDTOList = new ArrayList<>();

        long timestamp = System.currentTimeMillis();

        int[] joinUserIds = Arrays.stream(memberIds).mapToInt(Integer::valueOf).toArray();
        joinUserIds = ArrayUtils.removeElement(joinUserIds, currentUserId.intValue());

        Example example = new Example(GroupMembers.class);
        example.createCriteria().andEqualTo("memberId", currentUserId);
        List<GroupMembers> groupMembersList = groupMembersService.getByExample(example);

        if (groupMembersList != null && groupMembersList.size() >= Constants.MAX_USER_GROUP_OWN_COUNT) {
            throw new ServiceException(ErrorCode.INVALID_USER_GROUP_COUNT_LIMIT);
        }

        Example example1 = new Example(Users.class);
        example1.createCriteria().andIn("id", Arrays.asList(joinUserIds));
        List<Users> usersList = usersService.getByExample(example1);

        //开启了加入群验证，不允许直接加入群聊用户
        List<Integer> veirfyOpenedUserList = new ArrayList<>();
        //未开启加入群验证，允许直接加入群聊用户
        List<Integer> verifyClosedUserList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(usersList)) {
            for (Users users : usersList) {
                if (Users.GROUP_VERIFY_OPENED.equals(users.getGroupVerify())) {
                    veirfyOpenedUserList.add(users.getId());
                } else {
                    verifyClosedUserList.add(users.getId());
                }
            }
        }
        //创建群组
        Groups groups = new Groups();
        groups.setName(name);
        groups.setPortraitUri(portraitUri);
        //+1表示加上当前用户自己
        groups.setMemberCount(verifyClosedUserList.size() + 1);
        groups.setCreatorId(currentUserId);
        groups.setTimestamp(timestamp);
        groups.setCreatedAt(new Date());
        groups.setUpdatedAt(groups.getCreatedAt());
        groupsService.saveSelective(groups);


        List<Integer> megerUserIdList = new ArrayList<>(verifyClosedUserList);
        megerUserIdList.add(currentUserId);

        //构建返回结果
        for (int id : megerUserIdList) {
            UserStatusDTO userStatusDTO = new UserStatusDTO();
            userStatusDTO.setId(encode(id));
            userStatusDTO.setStatus(UserAddStatus.GROUP_ADDED.getCode());
            userStatusDTOList.add(userStatusDTO);
        }

        //批量保存或更新groupmember
        groupMembersService.batchSaveOrUpdate(groups.getId(), megerUserIdList, timestamp, currentUserId);

        //刷新dataversion GroupMember数据版本
        dataVersionsService.updateGroupMemberVersion(groups.getId(), timestamp);

        //调用融云接口创建群组
        List<String> encodeMemberIds = new ArrayList<>();
        for (int memberId : joinUserIds) {
            encodeMemberIds.add(encode(memberId));
        }
        try {
            //TODO
            rongCloudClient.createGroup(encode(groups.getId()), encodeMemberIds, name);
            //如果成功则调用融云接口发送通知
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //如果失败，插入GroupSync表进行记录 组信息同步失败记录
            groupSyncsService.saveOrUpdate(groups.getId(), false, false);
        }

        if (veirfyOpenedUserList.size() > 0) {
            for (int id : veirfyOpenedUserList) {
                UserStatusDTO userStatusDTO = new UserStatusDTO();
                userStatusDTO.setId(encode(id));
                userStatusDTO.setStatus(UserAddStatus.WAIT_MEMBER.getCode());
                userStatusDTOList.add(userStatusDTO);
            }
            //批量保存或更新GroupReceiver
            batchSaveOrUpdateGroupReceiver(groups, currentUserId, veirfyOpenedUserList, veirfyOpenedUserList, GroupReceivers.GROUP_RECEIVE_TYPE_MEMBER, GroupReceivers.GROUP_RECEIVE_STATUS_WAIT);
            //发送组应答消息 TODO
            sendGroupApplyMessage();
        }

        //清除缓存
        for (String memberId : memberIds) {
            CacheUtil.delete(CacheUtil.USER_GROUP_CACHE_PREFIX + memberId);
        }

        //构建返回结果
        GroupAddStatusDTO groupAddStatusDTO = new GroupAddStatusDTO();
        groupAddStatusDTO.setId(groups.getId());
        groupAddStatusDTO.setUserStatus(userStatusDTOList);
        return groupAddStatusDTO;
    }

    /**
     * 发送群申请消息 TODO
     */
    private void sendGroupApplyMessage() {
    }

    //TODO

    /**
     * 批量保存或更新GroupReceivers
     *
     * @param groups
     * @param requesterId
     * @param receiverIdList
     * @param operatorList
     * @param groupReceiveType
     * @param groupReceiveStatusW
     */
    private void batchSaveOrUpdateGroupReceiver(Groups groups, Integer requesterId, List<Integer> receiverIdList, List<Integer> operatorList, int groupReceiveType, int groupReceiveStatusW) {


    }

    /**
     * 添加群成员
     * <p>
     * 1、校验memberIds参数不为空
     * 2、根据groupId, currentUserId 查表GroupMember 判断当前用户是否有管理者角色（创建者or管理员）
     * 3、根据groupId 查询 groupVerify，groupDetail，获取目标群组是否开启了 入群认证
     * 4、根据memberIds 查询每个成员用户信息是否开启了个人入群认证
     * 5、处理 verifyOpendUserIds 开启认证的用户  更新为待用户处理状态, 并批量发消息
     * 6、处理 verifyClosedUserIds 关闭认证的用户
     * ====> 当自己不是管理者 && 群组开启了入群认证时, !hasManagerRole && isGroupVerifyOpened 需要管理员同意，更新为待管理员审批状态, 更新多个, 发消息
     * ====> 当自己是管理者 或者 群组没有开启入群认证时 !isGroupVerifyOpened || hasManagerRole 直接加群
     * 7、根据groupId, verifyClosedUserIds 删除群组退出列表delGroupExitedListItem
     *
     * @param currentUserId
     * @param groupId
     * @param memberIds
     */
    public List<UserStatusDTO> addMember(Integer currentUserId, String groupId, String[] memberIds) throws ServiceException {

        //返回结果对象
        List<UserStatusDTO> userStatusDTOList = new ArrayList<>();

        //查询当前用户在群组中的角色是不是管理者
        boolean hasManagerRole = true;
        Example example = new Example(GroupMembers.class);
        example.createCriteria().andEqualTo("groupId", groupId)
                .andEqualTo("memberId", currentUserId);

        GroupMembers groupMembers = groupMembersService.getOneByExample(example);
        if (groupMembers != null && !GroupRole.CREATOR.getCode().equals(groupMembers.getRole()) && !GroupRole.MANAGER.getCode().equals(groupMembers.getRole())) {
            hasManagerRole = false;
        }

        Groups groups = groupsService.getByPrimaryKey(Integer.valueOf(groupId));

        if (groups == null) {
            throw new ServiceException(ErrorCode.ILLEGAL_PARAMETER);
        }

        //群组是否开启了入群认证
        boolean isGroupVerifyOpened = Groups.CERTI_STATUS_OPENED.equals(groups.getCertiStatus()) ? true : false;

        //根据memberIds 查询每个成员用户信息是否开启了个人入群认证
        List<Integer> verifyOpendUserIds = new ArrayList<>();
        List<Integer> verifyClosedUserIds = new ArrayList<>();
        Example example1 = new Example(Users.class);

        int[] memberIdsInt = Arrays.stream(memberIds).mapToInt(Integer::valueOf).toArray();
        example1.createCriteria().andIn("id", Arrays.asList(memberIdsInt));
        List<Users> usersList = usersService.getByExample(example1);
        if (!CollectionUtils.isEmpty(usersList)) {
            for (Users u : usersList) {
                if (Users.GROUP_VERIFY_OPENED.equals(u.getGroupVerify())) {
                    verifyOpendUserIds.add(u.getId());
                } else {
                    verifyClosedUserIds.add(u.getId());
                }
            }
        }

        //处理 verifyOpendUserIds 开启认证的用户，更新为待用户处理状态, 并批量发消息
        if (verifyOpendUserIds.size() > 0) {
            Integer type = GroupReceivers.GROUP_RECEIVE_TYPE_MEMBER;
            for (Integer userId : verifyOpendUserIds) {
                UserStatusDTO userStatusDTO = new UserStatusDTO();
                userStatusDTO.setId(N3d.encode(userId));
                userStatusDTO.setStatus(UserAddStatus.WAIT_MEMBER.getCode());
                userStatusDTOList.add(userStatusDTO);

            }
            //TODO
            //更新为待用户处理状态, 并批量发消息
            batchSaveOrUpdateGroupReceiver(groups, currentUserId, verifyOpendUserIds, verifyOpendUserIds, type, GroupReceivers.GROUP_RECEIVE_STATUS_WAIT);
        }

        //处理 verifyClosedUserIds 关闭认证的用户
        if (verifyClosedUserIds.size() > 0) {

            //当自己不是管理者 && 群组开启了入群认证时, 需要管理员同意
            if (!hasManagerRole && isGroupVerifyOpened) {
                //更新为待管理员审批状态, 更新多个, 发消息
                Example example2 = new Example(GroupMembers.class);
                example2.createCriteria().andEqualTo("groupId", groupId)
                        .andIn("role", ImmutableList.of(GroupRole.MANAGER, GroupRole.CREATOR));
                //查询出所有管理者(Manager,Creator),
                List<GroupMembers> groupMembersList = groupMembersService.getByExample(example2);
                List<Integer> managerIds = new ArrayList<>();
                if (!CollectionUtils.isEmpty(groupMembersList)) {
                    for (GroupMembers groupMembers1 : groupMembersList) {
                        managerIds.add(groupMembers1.getMemberId());
                    }
                }

                Integer type = GroupReceivers.GROUP_RECEIVE_TYPE_MANAGER;
                for (Integer userId : verifyClosedUserIds) {
                    UserStatusDTO userStatusDTO = new UserStatusDTO();
                    userStatusDTO.setId(N3d.encode(userId));
                    userStatusDTO.setStatus(UserAddStatus.WAIT_MANAGER.getCode());
                    userStatusDTOList.add(userStatusDTO);
                }
                //更新为待管理员处理状态, 并批量发消息
                batchSaveOrUpdateGroupReceiver(groups, currentUserId, verifyClosedUserIds, managerIds, type, GroupReceivers.GROUP_RECEIVE_STATUS_WAIT);
            } else {
                //如果没有开启群验证或者有管理员角色 !isGroupVerifyOpened || hasManagerRole --> 直接加群
                for (Integer userId : verifyClosedUserIds) {
                    UserStatusDTO userStatusDTO = new UserStatusDTO();
                    userStatusDTO.setId(N3d.encode(userId));
                    userStatusDTO.setStatus(UserAddStatus.GROUP_ADDED.getCode());
                    userStatusDTOList.add(userStatusDTO);
                }
                addMember0(groupId, verifyClosedUserIds, currentUserId);
            }
            //删除群组退出列表
            groupExitedListsService.deleteGroupExitedListItems(groupId, verifyClosedUserIds);
        }
        return userStatusDTOList;

    }

    /**
     * 添加群成员到群 TODO
     *
     * @param groupId
     * @param verifyClosedUserIds
     * @param currentUserId
     */
    private void addMember0(String groupId, List<Integer> verifyClosedUserIds, Integer currentUserId) {

    }


    /**
     * 用户加入群组
     * <p>
     * 1、通过groupId 查询Groups信息，如果查询不到返回404，Unknown group.
     * 2、memberCount=memberCount+1，后计算组员数量是否超过限制，超过返回400错误
     * 3、根据groupId，update Group表中的memberCount、timestamp、批量保存或修改GroupMember
     * 4、刷新GroupMemberVersion数据版本
     * 5、调用融云接口join 加入群组方法，同步信息
     * ====》调用成功，获取昵称 并发送通知 sendGroupNotification
     * ====》保存信息到 GroupSync
     * 6、清除相关缓存
     * ====》Cache.del("user_groups_" + currentUserId);
     * ====》Cache.del("group_" + groupId);
     * ====》Cache.del("group_members_" + groupId);
     *
     * @param currentUserId
     * @param groupId
     * @param encodedGroupId
     */
    public void joinGroup(Integer currentUserId, Integer groupId, String encodedGroupId) throws ServiceException {

        long timestamp = System.currentTimeMillis();

        Groups groups = groupsService.getByPrimaryKey(groupId);
        if (groups == null) {
            throw new ServiceException(ErrorCode.GROUP_UNKNOWN_ERROR);
        }

        int memberCount = groups.getMemberCount() + 1;
        if (memberCount > groups.getMaxMemberCount()) {
            throw new ServiceException(ErrorCode.INVALID_GROUP_MEMNBER_MAX_COUNT);
        }

        //根据groupId，update Group表中的memberCount、timestamp、
        Groups newGroup = new Groups();
        newGroup.setId(groups.getId());
        newGroup.setTimestamp(timestamp);
        groupsService.updateByPrimaryKeySelective(newGroup);
        //批量保存或修改GroupMember
        groupMembersService.batchSaveOrUpdate(groupId, ImmutableList.of(currentUserId), timestamp, null);

        //刷新dataversion GroupMemberVersion数据版本
        dataVersionsService.updateGroupMemberVersion(groups.getId(), timestamp);

        //调用融云接口join 加入群组
        try {
            //TODO
            rongCloudClient.joinGroup(new String[]{N3d.encode(currentUserId)}, encodedGroupId, groups.getName());
            //如果成功则调用融云接口发送通知
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //如果失败，插入GroupSync表进行记录 组信息同步失败记录
            groupSyncsService.saveOrUpdate(groups.getId(), null, false);
        }

        //清除相关缓存
        CacheUtil.delete(CacheUtil.USER_GROUP_CACHE_PREFIX + currentUserId);
        CacheUtil.delete(CacheUtil.GROUP_CACHE_PREFIX + groupId);
        CacheUtil.delete(CacheUtil.GROUP_MEMBERS_CACHE_PREFIX + groupId);
    }


    /**
     * 设置群成员保护模式 TODO 普通组员也能执行这个方法吗？
     *
     * @param currentUserId
     * @param groupId
     * @param memberProtection
     */
    public void setMemberProtection(Integer currentUserId, Integer groupId, Integer memberProtection) throws ServiceException {

        String operation = "openMemberProtection";
        if (memberProtection == 0) {
            operation = "closeMemberProtection";
        }
        Groups groups = new Groups();
        groups.setId(groupId);
        groups.setMemberProtection(memberProtection);
        groupsService.updateByPrimaryKey(groups);

        CacheUtil.delete(CacheUtil.GROUP_CACHE_PREFIX + groupId);
        //发送群组通知
        try {
            sendGroupNtfMsg(currentUserId, groupId, operation, MessageType.GROUP_NOTIFICATION);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 发送群组消息
     *
     * @param currentUserId
     * @param groupId
     * @param operation
     * @param groupNotificationType
     * @throws ServiceException
     */
    private void sendGroupNtfMsg(Integer currentUserId, Integer groupId, String operation, MessageType groupNotificationType) throws ServiceException {

        String encodeUserId = N3d.encode(currentUserId);
        GroupMessage groupMessage = new GroupMessage();
        groupMessage.setSenderId(encodeUserId);
        groupMessage.setTargetId(MiscUtils.one2Array(N3d.encode(groupId)));
        groupMessage.setObjectName(MessageType.GROUP_NOTIFICATION.getObjectName());

        GroupNotificationMessage groupNotificationMessage = new GroupNotificationMessage(encodeUserId, operation, null, null, null);
        groupMessage.setContent(groupNotificationMessage);
        groupMessage.setIsIncludeSender(1);
//        isMentioned: 0, TODO  新版本没找到此字段
        rongCloudClient.sendGroupMessage(groupMessage);
    }

    /**
     * 获取退群列表
     *
     * @param currentUserId
     * @param groupId
     * @return
     * @throws ServiceException
     */
    public List<GroupExitedLists> getExitedList(Integer currentUserId, Integer groupId) throws ServiceException {

        Example example = new Example(GroupMembers.class);

        example.createCriteria().andEqualTo("groupId", groupId)
                .andEqualTo("memberId", currentUserId);
        GroupMembers groupMembers = groupMembersService.getOneByExample(example);
        if (groupMembers == null) {
            throw new ServiceException(ErrorCode.REQUEST_ERROR);
        }

        if (GroupRole.MEMBER.getCode().equals(groupMembers.getRole())) {
            throw new ServiceException(ErrorCode.NOT_GROUP_MANAGER);
        }

        Example example1 = new Example(GroupExitedLists.class);
        example1.createCriteria().andEqualTo("groupId", groupId);
        List<GroupExitedLists> groupExitedListsList = groupExitedListsService.getByExample(example1);

        return groupExitedListsList;

    }

    /**
     * 获取群成员信息
     *
     * @param groupId
     * @param memberId
     * @return
     * @throws ServiceException
     */
    public GroupMembers getMemberInfo(String groupId, String memberId) throws ServiceException {

        GroupMembers groupMembers = groupMembersService.getGroupMember(Integer.valueOf(groupId), Integer.valueOf(memberId));

        //TODO getDeleted
        if (groupMembers != null || groupMembers.getDeleted()) {
            throw new ServiceException(ErrorCode.NOT_GROUP_MEMBER);
        }

        return groupMembers;
    }

    /**
     * 设置群成员信息 设置哪个传哪个，不传为不设置
     *
     * @param groupId
     * @param memberId
     * @param groupNickname
     * @param region
     * @param phone
     * @param weChat
     * @param alipay
     * @param memberDesc
     */
    public void setMemberInfo(String groupId, String memberId, String groupNickname, String region, String phone, String weChat, String alipay, String[] memberDesc) throws ServiceException {

        GroupMembers groupMembers = groupMembersService.getGroupMember(Integer.valueOf(groupId), Integer.valueOf(memberId));

        if (groupMembers != null || groupMembers.getDeleted()) {
            throw new ServiceException(ErrorCode.NOT_GROUP_MEMBER);
        }

        //更新GroupMember 信息
        GroupMembers newGroupMembers = new GroupMembers();
        newGroupMembers.setId(groupMembers.getId());
        newGroupMembers.setGroupNickname(groupNickname);
        newGroupMembers.setRegion(region);
        newGroupMembers.setPhone(phone);
        newGroupMembers.setWeChat(weChat);
        newGroupMembers.setAlipay(alipay);
        newGroupMembers.setMemberDesc(JacksonUtil.toJson(memberDesc));

        groupMembersService.updateByPrimaryKeySelective(newGroupMembers);

        CacheUtil.delete(CacheUtil.GROUP_CACHE_PREFIX + groupId);
        CacheUtil.delete(CacheUtil.GROUP_MEMBERS_CACHE_PREFIX + groupId);
        return;
    }

    /**
     * 开启/更新 清理群离线消息
     *
     * @param currentUserId
     * @param groupId
     * @param clearStatus   清理选项 0-》关闭、 3-》清理 3 天前、 7-》清理 7 天前、 36-》清理 36 小时前
     */
    public void setRegularClear(Integer currentUserId, String groupId, Integer clearStatus) throws ServiceException {

        String operation = "openRegularClear";

        if (clearStatus == 0) {
            operation = "closeRegularClear";
        }

        GroupMembers groupMembers = groupMembersService.getGroupMember(Integer.valueOf(groupId), currentUserId);

        if (groupMembers == null || !GroupRole.CREATOR.getCode().equals(groupMembers.getRole())) {
            throw new ServiceException(ErrorCode.NOT_GROUP_OWNER);
        }

        Groups group = new Groups();
        group.setId(Integer.valueOf(groupId));
        group.setClearStatus(clearStatus);
        group.setClearTimeAt(System.currentTimeMillis());

        groupsService.updateByPrimaryKeySelective(group);
        //发送群组通知信息
        try {
            sendGroupNtfMsg(currentUserId, Integer.valueOf(groupId), operation, MessageType.CON_NOTIFICATION);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }

    }

    /**
     * 设置/取消 禁言状态
     *
     * @param currentUserId
     * @param groupId
     * @param muteStatus    禁言状态：0 关闭 1 开启
     * @param userId        可发言用户，不传全员禁言，仅群组和管理员可发言
     */
    public void setMuteAll(Integer currentUserId, Integer groupId, Integer muteStatus, String[] userId) {

        if (Groups.MUTE_STATUS_CLOSE.equals(muteStatus)) {
            //如果是取消禁言
            //调用融云接口 取消禁言 rongCloud.group.ban.remove

            Groups groups = new Groups();
            groups.setId(groupId);
            groups.setIsMute(muteStatus);
            groupsService.updateByPrimaryKeySelective(groups);

            CacheUtil.delete(CacheUtil.GROUP_CACHE_PREFIX + groupId);
            return;
        } else {
            //如果是开启全员禁言
            Example example = new Example(GroupMembers.class);
            example.createCriteria().andEqualTo("groupId", groupId)
                    .andIn("role", ImmutableList.of(GroupRole.CREATOR.getCode(), GroupRole.MANAGER.getCode()));
            List<GroupMembers> groupMembersList = groupMembersService.getByExample(example);

            if (!CollectionUtils.isEmpty(groupMembersList)) {

                //调用融云接口设置禁言rongCloud.group.ban.add

                //调用融云接口 将管理员添加到白名单rongCloud.group.ban.addWhitelist


                //Group更新


            }

        }

    }

    /**
     * 清空群验证通知消息
     *
     * @param currentUserId
     */
    public void clearNotice(Integer currentUserId) {

        Example example = new Example(GroupReceivers.class);
        example.createCriteria().andEqualTo("userId", currentUserId);
        groupReceiversService.deleteByExample(example);
        return;
    }

    /**
     * 根据userID获取群验证通知消息
     *
     * @param currentUserId
     * @return
     */
    public List<GroupReceivers> getNoticeInfo(Integer currentUserId) {

        Example example = new Example(GroupReceivers.class);
        example.createCriteria().andEqualTo("userId", currentUserId);
        example.setOrderByClause(" timestamp DESC ");
        return groupReceiversService.getByExample(example);

    }

    /**
     * 设置群认证
     *
     * @param currentUserId
     * @param groupId
     * @param certiStatus   认证状态： 0 开启(需要认证)、1 关闭（不需要认证）
     */
    public void setCertification(Integer currentUserId, Integer groupId, Integer certiStatus) throws ServiceException {

        GroupMembers groupMembers = groupMembersService.getGroupMember(groupId, currentUserId);

        if (groupMembers != null && isManagerRole(groupMembers.getRole())) {

            Groups groups = new Groups();
            groups.setCertiStatus(certiStatus);
            groups.setIsMute(groupId);
            groupsService.updateByPrimaryKeySelective(groups);
            CacheUtil.delete(CacheUtil.GROUP_CACHE_PREFIX + groupId);
        } else {
            throw new ServiceException(ErrorCode.NO_PERMISSION);
        }
    }

    private boolean isManagerRole(Integer role) {
        return GroupRole.CREATOR.getCode().equals(role) || GroupRole.MANAGER.getCode().equals(role);
    }


    public List<GroupMembers> getGroupMembers(Integer currentUserId, int groupId) throws ServiceException {

        String members = CacheUtil.get(CacheUtil.GROUP_MEMBERS_CACHE_PREFIX+groupId);
        if(members!=null){
//            return JacksonUtil.jsonToBean(members,null); TODO
            return null;
        }

        Example example = new Example(GroupMembers.class);
        example.createCriteria().andEqualTo("groupId",groupId);
        List<GroupMembers> groupMembersList = groupMembersService.getByExample(example);

        if(CollectionUtils.isEmpty(groupMembersList)){
            throw  new ServiceException(ErrorCode.GROUP_UNKNOWN_ERROR);
        }
        if(!isInGroupMember(groupMembersList,currentUserId)){
            throw new ServiceException(ErrorCode.NOT_GROUP_MEMBER_2);
        }

        CacheUtil.set(CacheUtil.GROUP_MEMBERS_CACHE_PREFIX+groupId,JacksonUtil.toJson(groupMembersList));

        return groupMembersList;

    }

    private boolean isInGroupMember(List<GroupMembers> groupMembersList,Integer userId){
        if(groupMembersList!=null){
            for(GroupMembers groupMembers:groupMembersList){
                if(groupMembers.getMemberId().equals(userId)){
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 根据groupId获取群信息
     *
     * @param currentUserId
     * @param groupId
     * @return
     */
    public Groups getGroupInfo(Integer currentUserId, Integer groupId) throws ServiceException {

        String groupJson = CacheUtil.get(CacheUtil.GROUP_CACHE_PREFIX+groupId);
        if(groupJson!=null){
//            return JacksonUtil.jsonToBean(groupJson,Groups.class); TODO
            return null;
        }

        Groups groups = groupsService.getByPrimaryKey(groupId);
        if(groups==null){
            throw new ServiceException(ErrorCode.GROUP_UNKNOWN_ERROR);
        }

        CacheUtil.set(CacheUtil.GROUP_CACHE_PREFIX+groupId,JacksonUtil.toJson(groups));

        return groups;
    }

    /**
     * 设置群名片
     * @param currentUserId
     * @param groupId
     * @param displayName
     */
    public void setDisPlayName(Integer currentUserId, Integer groupId, String displayName) throws ServiceException {

        long timestamp = System.currentTimeMillis();
        GroupMembers groupMembers = new GroupMembers();
        groupMembers.setDisplayName(displayName);
        groupMembers.setTimestamp(timestamp);
        Example example = new Example(GroupMembers.class);
        example.createCriteria().andEqualTo("groupId",groupId)
                .andEqualTo("memberId",currentUserId);
        int affectedCount = groupMembersService.updateByExampleSelective(groupMembers,example);

        if(affectedCount==0){
            throw new ServiceException(ErrorCode.GROUP_UNKNOWN_ERROR);
        }

        dataVersionsService.updateGroupVersion(currentUserId,timestamp);

        CacheUtil.delete(CacheUtil.GROUP_CACHE_PREFIX + groupId);


    }
}
