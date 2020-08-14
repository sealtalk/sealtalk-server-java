package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.constant.GroupRole;
import com.rcloud.server.sealtalk.dao.GroupMembersMapper;
import com.rcloud.server.sealtalk.domain.GroupMembers;
import com.rcloud.server.sealtalk.exception.ServiceException;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class GroupMembersService extends AbstractBaseService<GroupMembers, Integer> {

    @Resource
    private GroupMembersMapper mapper;

    @Override
    protected Mapper<GroupMembers> getMapper() {
        return mapper;
    }


    /**
     * 根据memberId查询用户所属组
     *
     * @param memberId 用户id
     * @return
     */
    public List<GroupMembers> queryGroupMembersWithGroupByMemberId(int memberId) {
        Example example = new Example(GroupMembers.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("memberId", memberId);
        return mapper.queryGroupMembersWithGroupByMemberId(example);
    }


    //TODO

    /**
     * 批量保存或更新GroupMembers
     *
     * @param groupId
     * @param memberIdList
     * @param timestamp
     * @param creatorId
     * @throws ServiceException
     */
    public void batchSaveOrUpdate(Integer groupId, List<Integer> memberIdList, long timestamp, Integer creatorId) throws ServiceException {
        Example example = new Example(GroupMembers.class);
        example.createCriteria().andEqualTo("groupId", groupId);

        List<GroupMembers> groupMembersList = this.getByExample(example);
        List<Integer> updateGroupMemberIds = new ArrayList<>();
        List<Integer> insertGroupMemberIds = new ArrayList<>();

        boolean creatorInMemebers = false;
        for (Integer memberId : memberIdList) {
            if (memberId == null) {
                throw new ServiceException(ErrorCode.REQUEST_ERROR);
            }
            boolean isUpdateMember = false;
            if (memberId.equals(creatorId)) {
                creatorInMemebers = true;
            }
            if (groupMembersList != null) {
                for (GroupMembers groupMembers : groupMembersList) {
                    if (groupMembers.getMemberId().equals(memberId)) {
                        isUpdateMember = true;
                        break;
                    }
                }
            }
            if (isUpdateMember) {
                updateGroupMemberIds.add(memberId);
            } else {
                insertGroupMemberIds.add(memberId);
            }
        }

        if (!creatorInMemebers) {
            throw new ServiceException(ErrorCode.INVALID_PARAM_CREATOR);
        }

        //TODO mybatis批量插入?
        if (updateGroupMemberIds.size() > 0) {

            for (Integer memerId : updateGroupMemberIds) {
                GroupMembers groupMembers = new GroupMembers();
                groupMembers.setRole(GroupRole.MEMBER.getCode());
                groupMembers.setDeleted(false);
                groupMembers.setTimestamp(timestamp);
                Example example1 = new Example(GroupMembers.class);
                example1.createCriteria().andEqualTo("groupId", groupId)
                        .andIn("memberId", updateGroupMemberIds);

                this.updateByExampleSelective(groupMembers, example);
            }
        }

        //TODO
        if (insertGroupMemberIds.size() > 0) {
            for (Integer memberId : insertGroupMemberIds) {
                GroupMembers groupMembers = new GroupMembers();
                groupMembers.setGroupId(groupId);
                groupMembers.setRole(memberId.equals(creatorId) ? GroupRole.CREATOR.getCode() : GroupRole.MANAGER.getCode());
                groupMembers.setTimestamp(timestamp);
                this.saveSelective(groupMembers);
            }
        }

        return;
    }

    /**
     * 根据groupId、memberId 查询 GroupMembers
     *
     * @param groupId
     * @param memberId
     * @return
     */
    public GroupMembers getGroupMember(Integer groupId, Integer memberId) {

        Example example = new Example(GroupMembers.class);

        example.createCriteria().andEqualTo("groupId", groupId)
                .andEqualTo("memberId", memberId);
        return this.getOneByExample(example);
    }

    /**
     * 根据groupId、userId 更新群组角色信息
     *
     * @param groupId
     * @param memberId
     * @param groupRole
     * @param timestamp
     */
    public void updateRoleAndTimestamp(Integer groupId, Integer memberId, GroupRole groupRole, long timestamp) {
        GroupMembers groupMembers = new GroupMembers();
        groupMembers.setRole(groupRole.getCode());
        groupMembers.setTimestamp(timestamp);
        Example example = new Example(GroupMembers.class);
        example.createCriteria().andEqualTo("groupId", groupId)
                .andEqualTo("memberId", memberId);
        this.updateByExampleSelective(groupMembers, example);
    }

    /**
     * 更新isDelete字段 TODO
     * @param groupId
     * @param memberId
     * @param isDeleted
     */
    public void updateDeleteStatus(Integer groupId, Integer memberId, boolean isDeleted) {
    }

    public void updateDeleteStatusAndRole(Integer groupId, Integer memberId, GroupRole role, long timestamp, boolean isDelete) {
    }

    public void updateDeleteStatus(Integer groupId, boolean isDelete,long timestamp) {
    }
}
