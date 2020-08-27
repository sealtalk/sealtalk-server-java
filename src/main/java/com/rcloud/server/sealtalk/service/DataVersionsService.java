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
public class DataVersionsService extends AbstractBaseService<DataVersions, Integer> {

    @Resource
    private DataVersionsMapper mapper;

    @Override
    protected Mapper<DataVersions> getMapper() {
        return mapper;
    }


    public void updateAllFriendshipVersion(Integer userId, long timestamp) {
        mapper.updateAllFriendshipVersion(userId, timestamp);
    }


    public void updateGroupMemberVersion(Integer groupId, long timestamp) {
        mapper.updateGroupMemberVersion(groupId, timestamp);
    }

    public void updateGroupVersion(Integer groupId, long timestamp) {

        mapper.updateGroupVersion(groupId,timestamp);

    }

    /**
     * 更新黑名单数据版本
     *
     * @param userId
     * @param timestamp
     */
    public void updateBlacklistVersion(Integer userId, long timestamp) {

        DataVersions dataVersions = new DataVersions();
        dataVersions.setUserId(userId);
        dataVersions.setBlacklistVersion(timestamp);
        this.updateByPrimaryKeySelective(dataVersions);
    }
}
