package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.FriendshipsMapper;
import com.rcloud.server.sealtalk.domain.Friendships;
import com.rcloud.server.sealtalk.domain.FriendshipsExample;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class FriendshipsService {

    @Resource
    private FriendshipsMapper mapper;

    public Friendships getInfo(Integer currentUserId, Integer friendId) {
        FriendshipsExample example = new FriendshipsExample()
            .createCriteria()
            .andUserIdEqualTo(currentUserId)
            .andFriendIdEqualTo(friendId)
            .example();
        return mapper.selectOneByExample(example);
    }
}
