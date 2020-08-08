package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.DataVersionsMapper;
import javax.annotation.Resource;

import com.rcloud.server.sealtalk.domain.DataVersions;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

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


}
