package com.rcloud.server.sealtalk.manager;

import com.google.common.collect.ImmutableList;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.constant.GroupRole;
import com.rcloud.server.sealtalk.constant.UserAddStatus;
import com.rcloud.server.sealtalk.domain.*;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.model.response.dto.GroupAddStatusDTO;
import com.rcloud.server.sealtalk.model.response.dto.UserStatusDTO;
import com.rcloud.server.sealtalk.rongcloud.RongCloudClient;
import com.rcloud.server.sealtalk.service.*;
import com.rcloud.server.sealtalk.util.CacheUtil;
import com.rcloud.server.sealtalk.util.N3d;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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


    public List<Groups> getGroupList(List<Integer> groupIds) {

        Example example = new Example(Groups.class);
        example.createCriteria().andIn("id", groupIds);
        return groupsService.getByExample(example);
    }

    //TODO 重点测试
    //TODO 重点测试
    @Transactional(rollbackFor = Exception.class)
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
            GroupSyncs groupSyncs = new GroupSyncs();
            groupSyncs.setGroupId(groups.getId());
            groupSyncs.setSyncInfo(false);
            groupSyncs.setSyncMember(false);
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

    //TODO


    /**
     * 发送群申请消息
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
}
