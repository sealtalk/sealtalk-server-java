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
import io.rong.models.Result;
import io.rong.models.message.GroupMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

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

    @Resource
    private GroupBulletinsService groupBulletinsService;

    @Resource
    private GroupFavsService groupFavsService;


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
            //如果失败，插入GroupSync表进行记录 组信息同步失败记录  TODO
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
            //sendGroupApplyMessage(currentUserId, userIdList, 0, GroupReceivers.GROUP_RECEIVE_STATUS_EXPIRED, groupId);
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
     *
     * @param currentUserId
     * @param userIdList
     * @param i
     * @param groupReceiveStatusExpired
     * @param groupId
     */
    private void sendGroupApplyMessage(Integer currentUserId, List<Integer> userIdList, int i, Integer groupReceiveStatusExpired, Integer groupId) {
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
        if (groupMembers == null || GroupMembers.IS_DELETED_YES.equals(groupMembers.getIsDeleted())) {
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

        if (groupMembers == null || GroupMembers.IS_DELETED_YES.equals(groupMembers.getIsDeleted())) {
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
     * 1、参数合法性验证，muteStatus校验失败返回400，Illegal parameter
     * 2、如果是取消禁言muteStatus == 0
     * ====》调用融云接口rongCloud.group.ban.remove，失败打印日志
     * ====》调用成功根据groupId更新Group的isMute字段，并清除缓存Cache.del("group_" + groupId)，然后返回
     * 3、如果是开启禁言
     * ====》根据groupId查询GroupMember ，查询出群主和管理员
     * ====》然后调用rongCloud.group.ban.add  设置禁言
     * ====》调用rongCloud.group.ban.addWhitelist将可发言用户加入白名单
     * 4、然后根据groupId更新Group的isMute
     * 5、然后清除缓存group_
     *
     * @param currentUserId
     * @param groupId
     * @param muteStatus    禁言状态：0 关闭 1 开启
     * @param userId        可发言用户，不传全员禁言，仅群组和管理员可发言
     */
    public void setMuteAll(Integer currentUserId, Integer groupId, Integer muteStatus, String[] userIds) throws ServiceException {

        if (Groups.MUTE_STATUS_CLOSE.equals(muteStatus)) {
            //如果是取消禁言
            //调用融云接口 取消禁言 rongCloud.group.ban.remove

            try {
                Result result = rongCloudClient.removeMuteStatus(N3d.encode(groupId));
                if (result.getCode() == 200) {
                    Groups groups = new Groups();
                    groups.setId(groupId);
                    groups.setIsMute(muteStatus);
                    groupsService.updateByPrimaryKeySelective(groups);

                    CacheUtil.delete(CacheUtil.GROUP_CACHE_PREFIX + groupId);
                }
                {
                    log.error("Error: rollback group failed on IM server, error,code: " + result.getCode());
                }

            } catch (Exception e) {
                log.error("Error: rollback group failed on IM server, error: " + e.getMessage(), e);
            }
            return;
        } else {
            //如果是开启全员禁言
            Example example = new Example(GroupMembers.class);
            example.createCriteria().andEqualTo("groupId", groupId)
                    .andIn("role", ImmutableList.of(GroupRole.CREATOR.getCode(), GroupRole.MANAGER.getCode()));
            List<GroupMembers> groupMembersList = groupMembersService.getByExample(example);

            List<Integer> memberIds = CollectionUtils.arrayToList(MiscUtils.covertString2Int(userIds));

            if (!CollectionUtils.isEmpty(groupMembersList)) {
                for (GroupMembers groupMembers : groupMembersList) {
                    memberIds.add(groupMembers.getMemberId());
                }
            }

            //调用融云接口设置禁言rongCloud.group.ban.add
            try {
                Result result = rongCloudClient.setMuteStatus(N3d.encode(groupId));
                if (result.getCode() == 200) {

                    try {
                        Result result1 = rongCloudClient.addGroupWhitelist(N3d.encode(groupId), MiscUtils.encodeIds(memberIds));

                        if (result1.getCode() == 200) {

                            Groups groups = new Groups();
                            groups.setId(groupId);
                            groups.setIsMute(muteStatus);
                            groupsService.updateByPrimaryKeySelective(groups);

                            CacheUtil.delete(CacheUtil.GROUP_CACHE_PREFIX + groupId);

                        } else {
                            log.error("Error: add group whitelist failed on IM server, error code={} " + result1.getCode());
                        }
                    } catch (Exception e) {
                        log.error("Error: add group whitelist failed on IM server, error: " + e.getMessage());
                    }
                } else {
                    log.error("Error: rollback group failed on IM server, error,code: " + result.getCode());
                }

            } catch (Exception e) {
                log.error("Error: rollback group failed on IM server, error: " + e.getMessage(), e);
            }
            return;
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

        String members = CacheUtil.get(CacheUtil.GROUP_MEMBERS_CACHE_PREFIX + groupId);
        if (members != null) {
//            return JacksonUtil.jsonToBean(members,null); TODO
            return null;
        }

        Example example = new Example(GroupMembers.class);
        example.createCriteria().andEqualTo("groupId", groupId);
        List<GroupMembers> groupMembersList = groupMembersService.getByExample(example);

        if (CollectionUtils.isEmpty(groupMembersList)) {
            throw new ServiceException(ErrorCode.GROUP_UNKNOWN_ERROR);
        }
        if (!isInGroupMember(groupMembersList, currentUserId)) {
            throw new ServiceException(ErrorCode.NOT_GROUP_MEMBER_2);
        }

        CacheUtil.set(CacheUtil.GROUP_MEMBERS_CACHE_PREFIX + groupId, JacksonUtil.toJson(groupMembersList));

        return groupMembersList;

    }

    private boolean isInGroupMember(List<GroupMembers> groupMembersList, Integer userId) {
        if (groupMembersList != null) {
            for (GroupMembers groupMembers : groupMembersList) {
                if (groupMembers.getMemberId().equals(userId)) {
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

        String groupJson = CacheUtil.get(CacheUtil.GROUP_CACHE_PREFIX + groupId);
        if (groupJson != null) {
//            return JacksonUtil.jsonToBean(groupJson,Groups.class); TODO
            return null;
        }

        Groups groups = groupsService.getByPrimaryKey(groupId);
        if (groups == null) {
            throw new ServiceException(ErrorCode.GROUP_UNKNOWN_ERROR);
        }

        CacheUtil.set(CacheUtil.GROUP_CACHE_PREFIX + groupId, JacksonUtil.toJson(groups));

        return groups;
    }

    /**
     * 设置群名片
     *
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
        example.createCriteria().andEqualTo("groupId", groupId)
                .andEqualTo("memberId", currentUserId);
        int affectedCount = groupMembersService.updateByExampleSelective(groupMembers, example);

        if (affectedCount == 0) {
            throw new ServiceException(ErrorCode.GROUP_UNKNOWN_ERROR);
        }

        dataVersionsService.updateGroupVersion(currentUserId, timestamp);

        CacheUtil.delete(CacheUtil.GROUP_CACHE_PREFIX + groupId);


    }

    /**
     * 设置群组头像地址
     *
     * @param currentUserId
     * @param groupId
     * @param portraitUri
     */
    public void setGroupPortraitUri(Integer currentUserId, Integer groupId, String portraitUri) throws ServiceException {
        long timestamp = System.currentTimeMillis();
        Groups groups = new Groups();
        groups.setPortraitUri(portraitUri);
        groups.setTimestamp(timestamp);

        Example example = new Example(Groups.class);
        example.createCriteria().andEqualTo("id", groupId)
                .andEqualTo("creatorId", currentUserId);
        int affectedCount = groupsService.updateByExampleSelective(groups, example);
        if (affectedCount == 0) {
            throw new ServiceException(ErrorCode.GROUP_OR_CREATOR_UNKNOW);
        }

        //刷新数据版本
        dataVersionsService.updateGroupVersion(currentUserId, timestamp);

        //删除缓存
        Example example1 = new Example(GroupMembers.class);
        example1.createCriteria().andEqualTo("groupId", groupId);
        List<GroupMembers> groupMembersList = groupMembersService.getByExample(example1);
        if (!CollectionUtils.isEmpty(groupMembersList)) {
            for (GroupMembers groupMembers : groupMembersList) {
                CacheUtil.delete(CacheUtil.USER_GROUP_CACHE_PREFIX + groupMembers.getMemberId());
            }
        }

        CacheUtil.delete(CacheUtil.GROUP_CACHE_PREFIX + groupId);
        return;
    }

    /**
     * 获取群公告
     *
     * @param currentUserId
     * @param groupId
     * @return
     */
    public GroupBulletins getBulletin(Integer currentUserId, Integer groupId) {

        Example example = new Example(GroupBulletins.class);
        example.createCriteria().andEqualTo("groupId", groupId);
        example.setOrderByClause(" timestamp DESC ");
        return groupBulletinsService.getOneByExample(example);
    }

    /**
     * 设置群公告
     *
     * @param currentUserId
     * @param groupId
     * @param bulletin
     */
    public void setBulletin(Integer currentUserId, Integer groupId, String bulletin) {
        long timestamp = System.currentTimeMillis();
        GroupBulletins groupBulletins = new GroupBulletins();
        groupBulletins.setGroupId(groupId);
        groupBulletins.setContent(bulletin);
        groupBulletins.setTimestamp(timestamp);
        groupBulletinsService.saveSelective(groupBulletins);

        Example example = new Example(GroupMembers.class);
        example.createCriteria().andEqualTo("groupId", groupId);
        List<GroupMembers> groupMembersList = groupMembersService.getByExample(example);
        if (!CollectionUtils.isEmpty(groupMembersList)) {
            for (GroupMembers groupMembers : groupMembersList) {
                CacheUtil.delete(CacheUtil.USER_GROUP_CACHE_PREFIX + groupMembers.getMemberId());
            }
        }

        CacheUtil.delete(CacheUtil.GROUP_CACHE_PREFIX + groupId);
        if (!StringUtils.isEmpty(bulletin)) {
            //获取当前用户昵称并发消息
            String nickName = usersService.getCurrentUserNickNameWithCache(currentUserId);
            //nickName没有用到？ TODO
            sendBulletinNotification(currentUserId, groupId, bulletin);
        }

        return;
    }

    /**
     * 发送公告通知
     *
     * @param currentUserId
     * @param groupId
     * @param bulletin
     */
    private void sendBulletinNotification(Integer currentUserId, Integer groupId, String bulletin) {
    }

    /**
     * 保存群组到通信录
     *
     * @param currentUserId
     * @param groupId
     */
    public void fav(Integer currentUserId, Integer groupId) throws ServiceException {

        GroupFavs groupFavs = new GroupFavs();
        groupFavs.setUserId(currentUserId);
        groupFavs.setGroupId(groupId);
        groupFavs.setCreatedAt(new Date());
        groupFavs.setUpdatedAt(groupFavs.getCreatedAt());
        try {
            groupFavsService.saveSelective(groupFavs);
        } catch (Exception e) {
            if (e.getCause() instanceof java.sql.SQLIntegrityConstraintViolationException) {
                throw new ServiceException(ErrorCode.ALREADY_EXISTS_GROUP);
            }
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
        return;
    }

    /**
     * 群组重命名
     * <p>
     * 1、根据groupId、currentUserId修改Group  name、timestamp，更新失败返回Unknown group or not creator.
     * 2、刷新GroupVersion 数据版本
     * 3、调用融云接口刷新name，rongCloud.group.refresh，调用失败，记录失败日志
     * 4、根据groupId更新GroupSync syncInfo=true
     * 5、获取昵称发送群组通知sendGroupNotification，通知类型 GROUP_OPERATION_RENAME
     * 6、根据groupId查询GroupMember 清除相关缓存 user_groups_、groups_
     * 7、根据groupId更新GroupReceiver的groupName
     *
     * @param currentUserId
     * @param groupId
     * @param name
     * @param encodedGroupId
     */
    public void rename(Integer currentUserId, Integer groupId, String name, String encodedGroupId) throws ServiceException {

        long timestamp = System.currentTimeMillis();

        Groups groups = new Groups();
        groups.setName(name);
        Example example = new Example(Groups.class);
        example.createCriteria().andEqualTo("id", groupId)
                .andEqualTo("creatorId", currentUserId);
        int affectedCount = groupsService.updateByExample(groups, example);

        if (affectedCount == 0) {
            throw new ServiceException(ErrorCode.GROUP_OR_CREATOR_UNKNOW);
        }
        dataVersionsService.updateGroupVersion(currentUserId, timestamp);

        try {
            Result result = rongCloudClient.refreshGroupName(encodedGroupId, name);

            if (result.getCode() != 200) {
                log.error("Error: refresh group info failed on IM server, code: {}", result.getCode());
            }


        } catch (Exception e) {
            log.error("Error: refresh group info failed on IM server, error: " + e.getMessage(), e);
        }

        groupSyncsService.saveOrUpdate(groups.getId(), true, null);

        String nickname = usersService.getCurrentUserNickNameWithCache(currentUserId);

        //发送组通知消息
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("operatorNickname", nickname);
        messageData.put("targetGroupName", name);
        messageData.put("timestamp", timestamp);

        sendGroupNotification(currentUserId, groupId, GroupNotificationType.GROUP_OPERATION_RENAME, messageData);

        Example example1 = new Example(GroupMembers.class);
        example1.createCriteria().andEqualTo("groupId", groupId);
        List<GroupMembers> groupMembersList = groupMembersService.getByExample(example1);
        if (!CollectionUtils.isEmpty(groupMembersList)) {
            for (GroupMembers groupMembers : groupMembersList) {
                CacheUtil.delete(CacheUtil.USER_GROUP_CACHE_PREFIX + groupMembers.getMemberId());
            }
        }

        CacheUtil.delete(CacheUtil.GROUP_CACHE_PREFIX + groupId);

        Example example2 = new Example(GroupReceivers.class);
        example.createCriteria().andEqualTo("groupId", groupId);
        GroupReceivers groupReceivers = new GroupReceivers();
        groupReceivers.setGroupName(name);
        groupReceiversService.updateByExampleSelective(groupReceivers, example2);

        return;
    }

    /**
     * 发送组通知 TODO
     *
     * @param currentUserId
     * @param groupId
     * @param operationType
     * @param messageData
     */
    private void sendGroupNotification(Integer currentUserId, Integer groupId, String operationType, Map<String, Object> messageData) {
    }

    /**
     * 批量删除群管理员
     *
     * @param currentUserId
     * @param groupId
     * @param memberIds
     * @param encodedMemberIds
     */
    public void batchRemoveManager(Integer currentUserId, Integer groupId, Integer[] memberIds, String[] encodedMemberIds) throws ServiceException {

        long timestamp = System.currentTimeMillis();
        setGroupMemberRole(groupId, memberIds, GroupRole.MEMBER, currentUserId, GroupNotificationType.GROUP_OPERATION_REMOVEMANAGER);
        dataVersionsService.updateGroupMemberVersion(groupId, timestamp);

        CacheUtil.delete(CacheUtil.GROUP_CACHE_PREFIX + groupId);
        CacheUtil.delete(CacheUtil.GROUP_MEMBERS_CACHE_PREFIX + groupId);

        Groups groups = groupsService.getByPrimaryKey(groupId);

        if (Groups.MUTE_STATUS_OPENED.equals(groups.getIsMute())) {
            //如果全员禁言
            groupReceiversService.deleteByMemberIds(groupId, memberIds);
            return;
        } else {
            try {
                Result result = rongCloudClient.removeGroupWhiteList(N3d.encode(groupId), encodedMemberIds);

                if (result.getCode() == 200) {
                    groupReceiversService.deleteByMemberIds(groupId, memberIds);
                } else {
                    log.error("invoke rongCloudClient removeWhiteList error,result.code={}", result.getCode());
                    throw new ServiceException(result.getCode(), result.getErrorMessage(), HttpStatusCode.CODE_200.getCode());
                }
            } catch (Exception e) {
                log.error("Error: remove group whitelist failed on IM server, error: {}" + e.getMessage(), e);
            }
        }
    }

    /**
     * 设置群成员角色
     *
     * @param groupId
     * @param memberIds
     * @param member
     * @param currentUserId
     * @param groupOperationRemovemanager
     */
    // TODO
    private void setGroupMemberRole(Integer groupId, Integer[] memberIds, GroupRole member, Integer currentUserId, String groupOperationRemovemanager) {
    }


    /**
     * 批量设置管理员
     * 1、参数合法性校验，判断自己是否在群组中，判断是否有权限设置管理员、判断设置的成员中是否有群主
     * 2、根据groupId、memberIds 修改GroupMember，role = GROUP_MANAGER
     * 3、获取相关昵称，调用接口发送群组通知 sendGroupNotification  通知类型=GROUP_OPERATION_SETMANAGER
     * 4、然后刷新GroupMemberVersion 数据版本
     * 5、然后清除缓存group_、group_members_
     * 6、根据groupId查询Group，isMute，如果isMute !=1，直接返回200
     * 7、调用融云服务接口rongCloud.group.ban.addWhitelist 添加白名单
     *
     * @param currentUserId
     * @param groupId
     * @param memberIds
     * @param encodedMemberIds
     * @throws ServiceException
     */
    public void batchSetManager(Integer currentUserId, Integer groupId, Integer[] memberIds, String[] encodedMemberIds) throws ServiceException {
        long timestamp = System.currentTimeMillis();

        setGroupMemberRole(groupId, memberIds, GroupRole.MANAGER, currentUserId, GroupNotificationType.GROUP_OPERATION_SETMANAGER);
        dataVersionsService.updateGroupMemberVersion(groupId, timestamp);
        CacheUtil.delete(CacheUtil.GROUP_CACHE_PREFIX + groupId);
        CacheUtil.delete(CacheUtil.GROUP_MEMBERS_CACHE_PREFIX + groupId);

        Groups groups = groupsService.getByPrimaryKey(groupId);

        if (!Groups.MUTE_STATUS_OPENED.equals(groups.getIsMute())) {
            return;
        }
        try {
            Result result = rongCloudClient.addGroupWhitelist(N3d.encode(groupId), encodedMemberIds);
            if (result.getCode() == 200) {
                return;
            } else {
                log.error("invoke rongCloudClient addWhitelist error,result.code={}", result.getCode());
                throw new ServiceException(result.getCode(), result.getErrorMessage(),HttpStatusCode.CODE_200.getCode());
            }
        } catch (Exception e) {
            log.error("Error: add group whitelist failed on IM server, error: {}" + e.getMessage(), e);
        }
        return;
    }


    /**
     * 转让群主
     * <p>
     * 1、判断userId === currentUserId ，不能自己转让角色给自己，否则返回403，Can not transfer creator role to yourself.
     * 2、通过currentUserId, userId查询GroupMember，判断返回结果数是否正确、判断当前用户是不是创建者，否则返回400错误
     * 3、根据groupId修改Groups的creatorId=userId、timestamp
     * 4、根据groupId、currentUserId修改GroupMember 把自己的角色修改成组员
     * 5、根据groupId、userId修改GroupMember 把userId的角色修改成创建者
     * 6、然后刷新GroupMemberVersion 数据版本
     * 7、执行添加IM黑白名单表addIMWhiteBlackList
     * 8、获取当前用户和useId的昵称，然后发送群组通知sendGroupNotification，通知类型GROUP_OPERATION_TRANSFER
     * 9、清除缓存group_、group_members_
     *
     * @param currentUserId
     * @param groupId
     * @param userId
     * @param encodedUserId
     * @throws ServiceException
     */
    public void transfer(Integer currentUserId, Integer groupId, Integer userId, String encodedUserId) throws ServiceException {

        long timestamp = System.currentTimeMillis();
        if (userId.equals(currentUserId)) {
            throw new ServiceException(ErrorCode.TRANSFER_TO_CREATOR_ERROR);
        }

        Example example = new Example(GroupMembers.class);
        example.createCriteria().andEqualTo("groupId", groupId)
                .andIn("memberId", ImmutableList.of(currentUserId, userId));
        List<GroupMembers> groupMembersList = groupMembersService.getByExample(example);

        if (groupMembersList == null || groupMembersList.size() != 2) {
            throw new ServiceException(ErrorCode.INVALID_GROUPID_USERID);
        }
        //变更群主
        transferGroupCreator0(currentUserId, groupId, userId, timestamp);

        //刷新然后刷新GroupMemberVersion数据版本
        dataVersionsService.updateGroupMemberVersion(groupId, timestamp);

        //根据groupId查询Groups
        Groups groups = groupsService.getByPrimaryKey(groupId);

        if (!Groups.MUTE_STATUS_OPENED.equals(groups.getIsMute())) {
            //如果全员禁言状态 否，直接删除GroupReverive 返回
            groupReceiversService.deleteGroupReverive(groupId, currentUserId);
            return;
        } else {
            //如果全员禁言状态 是，将新群主加入白名单，将当前用户(老群主)移除白名单
            try {
                //将新群主加入白名单
                Result result = rongCloudClient.addGroupWhitelist(N3d.encode(groupId), new String[]{N3d.encode(userId)});
                if (result.getCode() == 200) {
                    //将当前用户移除白名单
                    try {
                        Result result2 = rongCloudClient.removeGroupWhiteList(N3d.encode(groupId), new String[]{N3d.encode(currentUserId)});
                        if (result2.getCode() == 200) {
                            //根据groupId, currentUserId删除GroupReceiver
                            groupReceiversService.deleteGroupReverive(groupId, currentUserId);
                        }

                    } catch (Exception e) {
                        log.error("rongCloudClient removeGroupWhiteList error:" + e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                log.error("rongCloudClient addGroupWhitelist error: " + e.getMessage(), e);
            }
        }

    }


    /**
     * 变更群主 TODO  事务控制
     *
     * @param currentUserId
     * @param groupId
     * @param userId
     * @param timestamp
     */
    public void transferGroupCreator0(Integer currentUserId, Integer groupId, Integer userId, long timestamp) {
        Groups groups = new Groups();
        groups.setId(groupId);
        groups.setTimestamp(timestamp);
        groups.setCreatorId(userId);
        groupsService.updateByPrimaryKeySelective(groups);
        //变更当前用户角色为群组成员
        groupMembersService.updateRoleAndTimestamp(groupId, currentUserId, GroupRole.MEMBER, timestamp);
        //变更被转让用户角色为群主
        groupMembersService.updateRoleAndTimestamp(groupId, userId, GroupRole.CREATOR, timestamp);
    }


    /**
     * 解散群组
     * <p>
     * 1、获取当前用户昵称，发送群组通知 sendGroupNotification，通知类型 GROUP_OPERATION_DISMISS
     * 2、然后调用融云解散群组dismiss 接口，如果失败记录日志，然后返回500 , Quit failed on IM server.
     * 3、根据groupId、currentUserId修改Group memberCount=0，如果修改失败返回400，Unknown group or not creator.
     * 4、根据groupId删除Group，根据groupId 更新GroupMember isDeleted=true、timestamp
     * 5、刷新GroupMemberVersion数据版本
     * 6、根据groupId 查询所有GroupMember ，然后清除相关缓存user_groups_、group_、group_members_
     * 7、根据groupId删除GroupFav
     * 8、根据groupId查询所有GroupReceiver
     * 9、根据groupId更新GroupReceiver 状态status=GroupReceiverStatus.EXPIRED 过期
     * 10、然后发送群申请消息sendGroupApplyMessage
     *
     * @param currentUserId
     * @param groupId
     * @param encodedGroupId
     */
    public void dismiss(Integer currentUserId, Integer groupId, String encodedGroupId) throws ServiceException {

        long timestamp = System.currentTimeMillis();
        String currentUserNickName = usersService.getCurrentUserNickNameWithCache(currentUserId);
        //发送通知
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("operatorNickname", currentUserNickName);
        messageData.put("timestamp", timestamp);
        sendGroupNotification(currentUserId, groupId, GroupNotificationType.GROUP_OPERATION_DISMISS, messageData);

        try {
            Result result = rongCloudClient.dismiss(N3d.encode(currentUserId), encodedGroupId);
            if (result.getCode() != 200) {
                log.error("Error: dismiss group failed on IM server, code: {},errorMessage: {}", result.getCode(), result.getErrorMessage());
                throw new ServiceException(ErrorCode.QUIT_IM_SERVER_ERROR);
            } else {
                dismiss0(currentUserId, groupId, timestamp);
                //刷新数据版本
                dataVersionsService.updateGroupMemberVersion(groupId, timestamp);
                //清除相关缓存
                Example example = new Example(GroupMembers.class);
                example.createCriteria().andEqualTo("groupId", groupId);
                List<GroupMembers> groupMembersList = groupMembersService.getByExample(example);
                if (!CollectionUtils.isEmpty(groupMembersList)) {
                    for (GroupMembers groupMembers : groupMembersList) {
                        CacheUtil.delete(CacheUtil.USER_GROUP_CACHE_PREFIX + groupMembers.getMemberId());
                    }
                }
                CacheUtil.delete(CacheUtil.GROUP_CACHE_PREFIX + groupId);
                CacheUtil.delete(CacheUtil.GROUP_MEMBERS_CACHE_PREFIX + groupId);

                Example example1 = new Example(GroupFavs.class);
                example1.createCriteria().andEqualTo("groupId", groupId);
                groupFavsService.deleteByExample(example1);


                Example example2 = new Example(GroupReceivers.class);
                example2.createCriteria().andEqualTo("groupId", groupId);
                List<GroupReceivers> groupReceiversList = groupReceiversService.getByExample(example2);
                if (!CollectionUtils.isEmpty(groupReceiversList)) {
                    GroupReceivers groupReceivers = new GroupReceivers();
                    groupReceivers.setStatus(GroupReceivers.GROUP_RECEIVE_STATUS_EXPIRED);

                    Example example3 = new Example(GroupReceivers.class);
                    example3.createCriteria().andEqualTo("groupId", groupId);
                    groupReceiversService.updateByExampleSelective(groupReceivers, example3);

                    List<Integer> userIdList = new ArrayList<>();

                    for (GroupReceivers groupReceivers1 : groupReceiversList) {
                        userIdList.add(groupReceivers1.getUserId());
                    }
                    //TODO
                    //发送群申请消息sendGroupApplyMessage
                    sendGroupApplyMessage(currentUserId, userIdList, 0, GroupReceivers.GROUP_RECEIVE_STATUS_EXPIRED, groupId);

                    return;
                }
            }

        } catch (Exception e) {
            if (e instanceof ServiceException) {
                throw e;
            } else {
                log.error("rongCloudClient dismiss error: " + e.getMessage(), e);
                throw new ServiceException(ErrorCode.QUIT_IM_SERVER_ERROR);
            }
        }
    }

    //Todo 事务控制
    public void dismiss0(Integer currentUserId, Integer groupId, long timestamp) throws ServiceException {
        Groups groups = new Groups();
        groups.setMemberCount(0);
        Example example = new Example(Groups.class);
        example.createCriteria().andEqualTo("id", groupId)
                .andEqualTo("creatorId", currentUserId);
        int affectedCount = groupsService.updateByExampleSelective(groups, example);
        if (affectedCount == 0) {
            throw new ServiceException(ErrorCode.GROUP_OR_CREATOR_UNKNOW);
        }

        groupsService.deleteByPrimaryKey(groupId);

        GroupMembers groupMembers = new GroupMembers();
        groupMembers.setTimestamp(timestamp);
        groupMembers.setIsDeleted(GroupMembers.IS_DELETED_YES);
        Example example1 = new Example(GroupMembers.class);
        example1.createCriteria().andEqualTo("groupId", groupId);
        groupMembersService.updateByExampleSelective(groupMembers, example);
    }

    /**
     * 用户退出群组
     * 1、校验参数合法性
     * 2、根据groupId 查询群组信息，查询不到返回 404，Unknown group.
     * 3、根据groupId 查询GroupMember 组成员信息，判断当前用户是否是这个组的成员，不是返回403，Current user is not group member.
     * 4、如果当前要退出用户是该群创建者，并且该群除了创建者之外还有其他成员，则循环遍历选择一个成员作为新的创建者
     * 5、获取当前用户昵称，并发送群通知消息sendGroupNotification -》 消息类型 GROUP_OPERATION_QUIT
     * 6、调用融云服务接口 quit
     * ====》调用失败，返回500错误，Quit failed on IM server.
     * <p>
     * 7、调用quit接口后，分三种情况处理   (1)-(2)-(3) 在同一个事务里
     * (1)如果当前退出用户不是创建者
     * ====》根据groupId 更新Group的 memberCount=memberCount-1、timestamp
     * ====》根据groupId、currentUserId更新GroupMember 的isDeleted=true、timestamp
     * <p>
     * (2)如果当前退出用户是创建者，并且群成员数量大于1
     * ====》根据groupId 更新Group的 memberCount=memberCount-1、timestamp、creatorId=newCreatorId，创建者发生变更
     * ====》根据groupId、currentUserId更新GroupMember 的isDeleted=true、timestamp，role=GROUP_MEMBER,变为普通组员
     * ====》根据groupId、newCreatorId更新GroupMember 的timestamp，role=GROUP_CREATOR，变为群创建者
     * <p>
     * (3)如果当前退出用户是创建者，并且群成员数量不大于1(只有群主自己)
     * ====》resultMessage = 'Quit and group dismissed.' 解散群组
     * ====》根据groupId 更新Group的 memberCount=0 、timestamp
     * ====》根据groupId删除Group记录
     * ====》根据groupId、currentUserId更新GroupMember 的isDeleted=true、timestamp
     * <p>
     * 8、然后刷新数据版本GroupMemberVersion
     * 9、清除相关缓存 user_groups_、group_、group_members_
     * 10、根据groupId,currentUserId 删除GroupFav表
     * 11、根据groupId,currentUserId 保存或更新群组退出列表GroupExitedList
     *
     * @param currentUserId
     * @param groupId
     * @param encodedGroupId
     * @throws ServiceException
     */
    public String quitGroup(Integer currentUserId, Integer groupId, String encodedGroupId) throws ServiceException {

        long timestamp = System.currentTimeMillis();
        Groups groups = groupsService.getByPrimaryKey(groupId);
        if (groups == null) {
            throw new ServiceException(ErrorCode.GROUP_UNKNOWN_ERROR);
        }

        Example example = new Example(GroupMembers.class);
        example.createCriteria().andEqualTo("groupId", groupId);
        List<GroupMembers> groupMembersList = groupMembersService.getByExample(example);

        if (!isInGroupMember(groupMembersList, currentUserId)) {
            throw new ServiceException(ErrorCode.NOT_GROUP_MEMBER_3);
        }

        Integer newCreatorId = null;
        if (groups.getCreatorId().equals(currentUserId) && groups.getMemberCount() > 1) {
            //如果是群主退出，选择出新的群主
            for (GroupMembers groupMembers : groupMembersList) {
                if (!groupMembers.getMemberId().equals(currentUserId)) {
                    newCreatorId = groupMembers.getMemberId();
                }
            }
        }
        String nickName = usersService.getCurrentUserNickNameWithCache(currentUserId);

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("operatorNickname", nickName);
        String[] encodedMemberIds = new String[]{N3d.encode(currentUserId)};
        messageData.put("targetUserIds", encodedMemberIds);
        messageData.put("targetUserDisplayNames", new String[]{nickName});
        messageData.put("newCreatorId", newCreatorId);
        messageData.put("timestamp", timestamp);

        //发送退群通知
        sendGroupNotification(currentUserId, groupId, GroupNotificationType.GROUP_OPERATION_QUIT, messageData);

        //调用融云退群接口
        Result result = null;
        try {
            result = rongCloudClient.quitGroup(encodedMemberIds, encodedGroupId);
        } catch (Exception e) {
            log.error("rongCloudClient quitGroup error: " + e.getMessage(), e);
            throw new ServiceException(ErrorCode.QUIT_IM_SERVER_ERROR);
        }


        if (result != null && result.getCode() != 200) {
            log.error("Error: quit group failed on IM server, code: {}", result.getCode());
            throw new ServiceException(ErrorCode.QUIT_IM_SERVER_ERROR);
        }

        //分三种情况处理   (1)-(2)-(3) 在同一个事务里
        String resultMessage = quitGroup0(currentUserId, groupId, timestamp, groups, newCreatorId);

        //刷新GroupMemberVersion数据版本
        dataVersionsService.updateGroupMemberVersion(groupId, timestamp);

        //清除缓存
        CacheUtil.delete(CacheUtil.USER_GROUP_CACHE_PREFIX + currentUserId);
        CacheUtil.delete(CacheUtil.GROUP_CACHE_PREFIX + groupId);
        CacheUtil.delete(CacheUtil.GROUP_MEMBERS_CACHE_PREFIX + groupId);

        Example example1 = new Example(GroupFavs.class);
        example1.createCriteria().andEqualTo("groupId", groupId)
                .andEqualTo("userId", currentUserId);
        groupFavsService.deleteByExample(example1);

        //保存或更新群组退出列表 TODO
        groupExitedListsService.saveOrUpdate(groupId, currentUserId, currentUserId, 0);


        return resultMessage;
    }

    //TODO 事务处理
    private String quitGroup0(Integer currentUserId, Integer groupId, long timestamp, Groups groups, Integer newCreatorId) {
        String resultMessage = null;
        if (!groups.getCreatorId().equals(currentUserId)) {
            // (1)如果当前退出用户不是创建者
            resultMessage = "Quit.";
            groupsService.updateMemberCount(groupId, groups.getMemberCount() - 1, timestamp);
            groupMembersService.updateDeleteStatus(groupId, currentUserId, true);

        } else if (groups.getMemberCount() > 1) {
            //(2)如果当前退出用户是创建者，并且群成员数量大于1
            resultMessage = "Quit and group owner transfered.";
            groupsService.updateMemberCountAndCreatorId(groupId, groups.getMemberCount() - 1, timestamp, newCreatorId);
            groupMembersService.updateDeleteStatusAndRole(groupId, currentUserId, GroupRole.MEMBER, timestamp, true);
            groupMembersService.updateRoleAndTimestamp(groupId, currentUserId, GroupRole.CREATOR, timestamp);

        } else {
            //(3)如果当前退出用户是创建者，并且群成员数量不大于1(只有群主自己),那么退群并解散！
            resultMessage = "Quit and group dismissed.";
            groupsService.updateMemberCount(groupId, 0, timestamp);
            groupsService.deleteByPrimaryKey(groupId);
            groupMembersService.updateDeleteStatus(groupId, true, timestamp);
        }

        return resultMessage;
    }

    /**
     * 群主或群管理将群成员踢出群组
     * 1、校验参数合法性，不能踢出自己、不能踢出创建者
     * 2、根据groupId 查询 GroupMember，判断当前用户是否有权限踢人(只有群创建者或管理者才有权限踢人)
     * 3、校验memberIds 是否有空的情况、是否存在不是当前群的的成员Id
     * 4、获取memberIds 对应的用户昵称
     * 5、获取当前用户的昵称
     * 6、根据groupI更新Group的 memberCount、timestamp
     * 7、根据groupId，memberIds 更新GroupMember的isDeleted=true、timestamp
     * 8、刷新GroupMemberVersion数据版本
     * 9、发送组通知消息sendGroupNotification
     * 10、调用融云服务接口 quit
     * ====》调用失败，返回500错误，Quit failed on IM server.  调用失败不用回滚本地数据库！！！ 打印日志！！！！
     * 11、清除相关缓存 user_groups_、group_、group_members_
     * 12、根据groupId, memberIds 删除GroupFav表
     * 13、根据memberIds 循环保存或更新群组退出列表GroupExitedList
     *
     * @param currentUserId
     * @param groupId
     * @param encodeGroupId
     * @param memberIds
     * @param encodeMemberIds
     */
    public void kickMember(Integer currentUserId, Integer groupId, String encodeGroupId, String[] memberIds, String[] encodeMemberIds) throws ServiceException {

        long timestamp = System.currentTimeMillis();
        if (ArrayUtils.contains(memberIds, currentUserId)) {
            throw new ServiceException(ErrorCode.CAN_NOT_KICK_YOURSELF);
        }

        Groups groups = groupsService.getByPrimaryKey(groupId);

        if (groups == null) {
            throw new ServiceException(ErrorCode.GROUP_UNKNOWN_ERROR);
        }

        if (ArrayUtils.contains(memberIds, groups.getCreatorId())) {
            throw new ServiceException(ErrorCode.CAN_NOT_KICK_CREATOR);
        }

        Example example = new Example(GroupMembers.class);
        example.createCriteria().andEqualTo("groupId", groupId);

        List<GroupMembers> groupMembersList = groupMembersService.getByExample(example);

        Integer role = null;
        if (!CollectionUtils.isEmpty(groupMembersList)) {
            for (GroupMembers groupMembers : groupMembersList) {
                if (groupMembers.getMemberId().equals(currentUserId)) {
                    role = groupMembers.getRole();
                }
            }

            if (!GroupRole.MANAGER.getCode().equals(role) && !GroupRole.CREATOR.getCode().equals(role)) {
                throw new ServiceException(ErrorCode.NOT_GROUP_MEMBER_3);
            }
        } else {
            throw new ServiceException(ErrorCode.GROUP_MEMBER_EMPTY);
        }

        List<Integer> dbMemberIdList = new ArrayList<>();
        for (GroupMembers groupMembers : groupMembersList) {
            dbMemberIdList.add(groupMembers.getMemberId());
        }

        List<Integer> memberIdInts = new ArrayList<>();
        for (String memberId : memberIds) {
            if (StringUtils.isEmpty(memberId)) {
                throw new ServiceException(ErrorCode.EMPTY_MEMBERID);
            }
            if (!dbMemberIdList.contains(Integer.valueOf(memberId))) {
                throw new ServiceException(ErrorCode.CANT_NOT_KICK_NONE_MEMBER);
            }
            memberIdInts.add(Integer.valueOf(memberId));
        }

        kickMember0(groupId, memberIds, timestamp, groups);

        //刷新GroupMemberVersion数据版本
        dataVersionsService.updateGroupMemberVersion(groupId, timestamp);

        String nickname = usersService.getCurrentUserNickNameWithCache(currentUserId);

        List<Users> usersList = usersService.getUsers(memberIdInts);

        List<String> nicknameList = new ArrayList<>();
        for (Users u : usersList) {
            nicknameList.add(u.getNickname());
        }

        //发送组通知消息
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("operatorNickname", nickname);
        messageData.put("targetUserIds", encodeMemberIds);
        messageData.put("targetUserDisplayNames", nicknameList);
        messageData.put("timestamp", timestamp);
        sendGroupNotification(currentUserId, groupId, GroupNotificationType.GROUP_OPERATION_KICKED, messageData);

        //调用融云退出接口
        rongCloudClient.quitGroup(encodeMemberIds, encodeGroupId);

        //调用融云退群接口
        Result result = null;
        try {
            result = rongCloudClient.quitGroup(encodeMemberIds, encodeGroupId);
        } catch (Exception e) {
            log.error("rongCloudClient quitGroup error: " + e.getMessage(), e);
            throw new ServiceException(ErrorCode.QUIT_IM_SERVER_ERROR);
        }

        if (result != null && result.getCode() != 200) {
            log.error("Error: quit group failed on IM server, code: {}", result.getCode());
            throw new ServiceException(ErrorCode.QUIT_IM_SERVER_ERROR);
        }

        //清除相关缓存
        for (Integer memberId : memberIdInts) {
            CacheUtil.delete(CacheUtil.USER_GROUP_CACHE_PREFIX + memberId);
        }
        CacheUtil.delete(CacheUtil.GROUP_CACHE_PREFIX + groupId);
        CacheUtil.delete(CacheUtil.GROUP_MEMBERS_CACHE_PREFIX + groupId);

        //删除groupFav
        Example example1 = new Example(GroupFavs.class);
        example1.createCriteria().andEqualTo("groupId", groupId)
                .andEqualTo("memberId", memberIdInts);
        groupFavsService.deleteByExample(example1);

        //
        for (Integer memberId : memberIdInts) {
            groupExitedListsService.saveOrUpdate(groupId, memberId, currentUserId, 1);
        }
        return;

    }

    //TODO 事务
    private void kickMember0(Integer groupId, String[] memberIds, long timestamp, Groups groups) {
        groupsService.updateMemberCount(groupId, groups.getMemberCount() - memberIds.length, timestamp);

        List<Integer> memberIdIntList = CollectionUtils.arrayToList(MiscUtils.covertString2Int(memberIds));

        Example example1 = new Example(GroupMembers.class);
        example1.createCriteria().andEqualTo("groupId", groupId)
                .andIn("memberId", memberIdIntList);
        GroupMembers groupMembers = new GroupMembers();
        groupMembers.setIsDeleted(GroupMembers.IS_DELETED_YES);
        groupMembers.setTimestamp(timestamp);
        groupMembersService.updateByExampleSelective(groupMembers, example1);
    }
}