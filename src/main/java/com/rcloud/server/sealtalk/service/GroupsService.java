package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.GroupsMapper;
import com.rcloud.server.sealtalk.domain.Groups;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import javax.annotation.Resource;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class GroupsService extends AbstractBaseService<Groups, Integer> {

    @Resource
    private GroupsMapper mapper;

    @Override
    protected Mapper<Groups> getMapper() {
        return mapper;
    }

    /**
     * 根据groupId更新群成员数量
     * @param groupId
     * @param memberCount
     * @param timestamp
     */
    public void updateMemberCount(Integer groupId, int memberCount, long timestamp) {
        Groups groups = new Groups();
        groups.setId(groupId);
        groups.setMemberCount(memberCount);
        groups.setTimestamp(timestamp);
        this.updateByPrimaryKeySelective(groups);
    }

    /**
     * 更新memberCount 和creatorId
     * @param groupId
     * @param memberCount
     * @param timestamp
     * @param creatorId
     */
    public void updateMemberCountAndCreatorId(Integer groupId, int memberCount, long timestamp, Integer creatorId) {
        Groups groups = new Groups();
        groups.setId(groupId);
        groups.setMemberCount(memberCount);
        groups.setTimestamp(timestamp);
        groups.setCreatorId(creatorId);
        this.updateByPrimaryKeySelective(groups);
    }
}
