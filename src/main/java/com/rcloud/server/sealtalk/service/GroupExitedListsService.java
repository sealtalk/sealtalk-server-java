package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.GroupExitedListsMapper;
import com.rcloud.server.sealtalk.domain.GroupExitedLists;
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
public class GroupExitedListsService extends AbstractBaseService<GroupExitedLists, Integer> {

    @Resource
    private GroupExitedListsMapper mapper;

    @Override
    protected Mapper<GroupExitedLists> getMapper() {
        return mapper;
    }
}
