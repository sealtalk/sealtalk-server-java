package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.FriendshipsMapper;
import com.rcloud.server.sealtalk.domain.Friendships;

import javax.annotation.Resource;

import com.rcloud.server.sealtalk.domain.Users;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class FriendshipsService extends AbstractBaseService<Friendships,Integer>{

    @Resource
    private FriendshipsMapper mapper;

    @Override
    protected Mapper<Friendships> getMapper() {
        return mapper;
    }

    public Friendships getInfo(Integer currentUserId, Integer friendId) {
//        FriendshipsExample example = new FriendshipsExample()
//            .createCriteria()
//            .andUserIdEqualTo(currentUserId)
//            .andFriendIdEqualTo(friendId)
//            .example();
//        return mapper.selectOneByExample(example);
        return null;
    }

}
