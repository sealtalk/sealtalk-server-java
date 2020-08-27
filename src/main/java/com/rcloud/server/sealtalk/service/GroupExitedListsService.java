package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.GroupExitedListsMapper;
import com.rcloud.server.sealtalk.domain.GroupExitedLists;
import com.rcloud.server.sealtalk.domain.Groups;
import com.rcloud.server.sealtalk.domain.Users;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class GroupExitedListsService extends AbstractBaseService<GroupExitedLists, Integer> {

    @Resource
    private GroupExitedListsMapper mapper;

    @Override
    protected Mapper<GroupExitedLists> getMapper() {
        return mapper;
    }


    /**
     * 保存群退出列表
     *
     * @param groups  群组
     * @param quitUserList 退出用户列表
     * @param operatorUser 操作用户
     * @param quitReason 退出原因   0 群主踢出 ,1 管理员踢出, 2 主动退出
     *
     *
     */
    public void saveGroupExitedListItems(Groups groups, List<Users> quitUserList, Users operatorUser, Integer quitReason) {

        long timestamp = System.currentTimeMillis();
        for(Users users:quitUserList){
            GroupExitedLists groupExitedLists = new GroupExitedLists();
            groupExitedLists.setGroupId(groups.getId());
            groupExitedLists.setQuitUserId(users.getId());
            groupExitedLists.setQuitNickname(users.getNickname());
            groupExitedLists.setQuitPortraitUri(users.getPortraitUri());
            groupExitedLists.setQuitReason(quitReason);
            groupExitedLists.setQuitTime(timestamp);
            if(!GroupExitedLists.QUITE_REASON_SELF.equals(quitReason)){
                groupExitedLists.setOperatorId(operatorUser.getId());
                groupExitedLists.setOperatorName(operatorUser.getNickname());
            }
            groupExitedLists.setCreatedAt(new Date());
            groupExitedLists.setUpdatedAt(groupExitedLists.getCreatedAt());

            this.save(groupExitedLists);
        }
    }

    /**
     * 删除群组退出列表
     *
     * @param groupId
     * @param quitUserIds
     */
    public void deleteGroupExitedListItems(Integer groupId, List<Integer> quitUserIds) {

        Example example = new Example(GroupExitedLists.class);
        example.createCriteria().andEqualTo("groupId", groupId)
                .andIn("quitUserId", quitUserIds);
        this.deleteByExample(example);

    }


}
