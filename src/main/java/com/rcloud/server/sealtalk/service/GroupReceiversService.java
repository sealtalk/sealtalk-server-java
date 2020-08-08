package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.GroupReceiversMapper;
import javax.annotation.Resource;

import com.rcloud.server.sealtalk.domain.GroupReceivers;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class GroupReceiversService extends AbstractBaseService<GroupReceivers,Integer> {

    @Resource
    private GroupReceiversMapper mapper;

    @Override
    protected Mapper<GroupReceivers> getMapper() {
        return mapper;
    }
}
