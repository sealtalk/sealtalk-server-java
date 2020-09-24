package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.GroupsMapper;
import com.rcloud.server.sealtalk.domain.Groups;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tk.mybatis.mapper.common.Mapper;

import javax.annotation.Resource;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Slf4j
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
    public void updateMemberCount(Integer groupId, Integer memberCount, Long timestamp) {
        Assert.notNull(groupId,"groupId is null");
        Assert.notNull(memberCount,"memberCount is null");
        Assert.notNull(timestamp,"timestamp is null");
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
    public void updateMemberCountAndCreatorId(Integer groupId, Integer memberCount, Long timestamp, Integer creatorId) {
        Assert.notNull(groupId,"groupId is null");
        Assert.notNull(memberCount,"memberCount is null");
        Assert.notNull(timestamp,"timestamp is null");
        Assert.notNull(creatorId,"creatorId is null");

        Groups groups = new Groups();
        groups.setId(groupId);
        groups.setMemberCount(memberCount);
        groups.setTimestamp(timestamp);
        groups.setCreatorId(creatorId);
        this.updateByPrimaryKeySelective(groups);
    }
}
