package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.DataVersionsMapper;
import com.rcloud.server.sealtalk.domain.DataVersions;
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
public class DataVersionsService extends AbstractBaseService<DataVersions,Integer> {

    @Resource
    private DataVersionsMapper mapper;

    @Override
    protected Mapper<DataVersions> getMapper() {
        return mapper;
    }


    public void updateAllFriendshipVersion(Integer userId){
        long timestamp = System.currentTimeMillis();
        mapper.updateAllFriendshipVersion(userId,timestamp);

    }


    public void updateGroupMemberVersion(Integer groupId, long timestamp) {

        //TODO
        //UPDATE data_versions d JOIN group_members g ON d.userId = g.memberId AND g.groupId = ? AND g.isDeleted = 0 SET d.groupVersion = ?, d.groupMemberVersion = ?
        mapper.updateGroupMemberVersion(groupId,timestamp);
    }

    public void updateGroupVersion(Integer currentUserId, long timestamp) {
//UPDATE data_versions d JOIN group_members g ON d.userId = g.memberId AND g.groupId = ? AND g.isDeleted = 0 SET d.groupVersion = ?
//TODO
    }
}
