package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.FriendshipsMapper;
import com.rcloud.server.sealtalk.domain.Friendships;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class FriendshipsService extends AbstractBaseService<Friendships, Integer> {

    @Resource
    private FriendshipsMapper mapper;

    @Override
    protected Mapper<Friendships> getMapper() {
        return mapper;
    }


    public void saveOrUpdate(Friendships friendship, Integer userId, Integer friendId) {

        Example example = new Example(Friendships.class);
        example.createCriteria().andEqualTo("userId", userId)
                .andEqualTo("friendId", friendId);
        Friendships f = this.getOneByExample(example);
        if (f == null) {
            this.save(friendship);
        } else {
            this.updateByExample(friendship, example);
        }
    }

    public int updateAgreeStatus(Integer userId, Integer friendId, long timestamp, List<Integer> statusList) {

        Example example = new Example(Friendships.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId)
                .andEqualTo("friendId", friendId);
        if (!CollectionUtils.isEmpty(statusList)) {
            criteria.andIn("status", statusList);
        }
        Friendships friendships = new Friendships();
        friendships.setStatus(Friendships.FRIENDSHIP_AGREED);
        friendships.setTimestamp(timestamp);
        return this.updateByExampleSelective(friendships, example);
    }

    public List<Friendships> getFriendShipListWithUsers(Integer currentUserId) {
        return mapper.getFriendShipListWithUsers(currentUserId);
    }

    public Friendships getFriendShipWithUsers(Integer currentUserId, int friendId, int status) {
        return mapper.getFriendShipWithUsers(currentUserId,friendId,status);
    }

    /**
     * 更新好友关系状态为黑名单状态
     *
     * @param currentUserId
     * @param friendId
     */
    public void updateFriendShipBlacklistsStatus(Integer currentUserId, Integer friendId) {

        //更新Friendship 表状态信息为 FRIENDSHIP_BLACK = 31
        Friendships friendships = new Friendships();
        friendships.setDisplayName("");
        friendships.setMessage("");
        friendships.setTimestamp(System.currentTimeMillis());
        friendships.setStatus(Friendships.FRIENDSHIP_PULLEDBLACK);

        Example example = new Example(Friendships.class);
        example.createCriteria().andEqualTo("friendId", friendId)
                .andEqualTo("userId", currentUserId)
                .andEqualTo("status", Friendships.FRIENDSHIP_AGREED);
        this.updateByExampleSelective(friendships, example);
    }

    public List<Friendships> getFriendShipListWithUsers(Integer currentUserId, Long version) {
        return null;//TODO
    }

    public Friendships getOneByUserIdAndFriendId(Integer currentUserId, Integer friendId) {
        Friendships f = new Friendships();
        f.setUserId(currentUserId);
        f.setFriendId(friendId);
        return this.getOne(f);

    }
}
