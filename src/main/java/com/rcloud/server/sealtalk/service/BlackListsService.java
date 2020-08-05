package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.BlackListsMapper;
import com.rcloud.server.sealtalk.domain.BlackLists;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class BlackListsService {

    @Resource
    private BlackListsMapper mapper;

    public BlackLists queryOne(Integer currentUserId, Integer friendId) {
//        BlackListsExample example = new BlackListsExample()
//            .createCriteria()
//            .andFriendIdEqualTo(currentUserId)
//            .andUserIdEqualTo(friendId)
//            .example();
//        return mapper.selectOneByExample(example);

        return null;
    }
}
