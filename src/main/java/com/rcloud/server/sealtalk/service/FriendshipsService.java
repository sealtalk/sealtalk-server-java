package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.FriendshipsMapper;
import com.rcloud.server.sealtalk.domain.Friendships;
import io.swagger.models.auth.In;
import org.springframework.stereotype.Service;
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

    //TODO
    public void saveOrUpdate(Friendships friendship, Integer userId, Integer friendId) {

        Example example = new Example(Friendships.class);
        example.createCriteria().andEqualTo("userId",userId)
                                .andEqualTo("friendId",friendId);
        Friendships f = this.getOneByExample(example);
        if(f==null){
            this.save(friendship);
        }else {
            this.updateByExample(friendship,example);
        }
    }

    public int updateAgreeStatus(Integer userId, Integer friendId, long timestamp, List<Integer> statusList) {

        Example example = new Example(Friendships.class);
        example.createCriteria().andEqualTo("userId",userId)
                .andEqualTo("friendId",friendId)
                .andIn("status",statusList);
        Friendships friendships = new Friendships();
        friendships.setStatus(Friendships.FRIENDSHIP_AGREED);
        friendships.setTimestamp(timestamp);
        return this.updateByExampleSelective(friendships,example);
    }

    public List<Friendships> getFriendShipListWithUsers(Integer currentUserId) {
        //TODO
        return null;
    }

    //TODO
    public Friendships getFriendShipWithUsers(Integer currentUserId, int friendId,int status) {
        return null;
    }
}
