package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.BlackListsMapper;
import com.rcloud.server.sealtalk.domain.BlackLists;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class BlackListsService extends AbstractBaseService<BlackLists, Integer> {

    @Resource
    private BlackListsMapper mapper;

    @Override
    protected Mapper<BlackLists> getMapper() {
        return mapper;
    }


    /**
     * 根据userId、friendId保存或更新BlackLists
     *
     * @param userId
     * @param friendId
     * @param status
     * @param timestamp
     */
    public void saveOrUpdate(Integer userId, Integer friendId, Integer status, long timestamp) {
        Example example = new Example(BlackLists.class);
        example.createCriteria().andEqualTo("userId", userId)
                .andEqualTo("friendId", friendId);

        BlackLists blackLists = mapper.selectOneByExample(example);
        if (blackLists == null) {
            blackLists = new BlackLists();
            blackLists.setUserId(userId);
            blackLists.setFriendId(friendId);
            blackLists.setStatus(status);
            blackLists.setTimestamp(timestamp);
            blackLists.setCreatedAt(new Date());
            blackLists.setUpdatedAt(blackLists.getCreatedAt());
            this.saveSelective(blackLists);
        } else {
            BlackLists newBlackLists = new BlackLists();
            newBlackLists.setId(blackLists.getId());
            newBlackLists.setStatus(status);
            newBlackLists.setTimestamp(timestamp);
            newBlackLists.setUpdatedAt(new Date());
            this.updateByPrimaryKeySelective(newBlackLists);
        }
    }


    /**
     * 获取UserId获取对应的黑名单列表，包含Users
     *
     * @param currentUserId
     * @return
     */
    public List<BlackLists> getBlackListsWithFriendUsers(Integer currentUserId) {
        return mapper.selectBlackListsWithFriendUsers(currentUserId);
    }

    /**
     * 根据userId、friendId更新黑名单状态
     *
     * @param userId
     * @param friendId
     * @param status
     * @param timestamp
     */
    public void updateStatus(Integer userId, Integer friendId, Integer status, long timestamp) {

        BlackLists blackLists = new BlackLists();
        blackLists.setUpdatedAt(new Date());
        blackLists.setStatus(status);
        blackLists.setTimestamp(timestamp);
        Example example = new Example(BlackLists.class);
        example.createCriteria().andEqualTo("userId", userId)
                .andEqualTo("friendId", friendId);

        this.updateByExampleSelective(blackLists, example);

    }

    public List<BlackLists> getBlackListsWithFriendUsers(Integer currentUserId, Long timestamp) {

        return null;//TODO
    }
}
