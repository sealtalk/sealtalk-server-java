package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.UsersMapper;
import com.rcloud.server.sealtalk.domain.Users;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class UsersService {

    @Resource
    private UsersMapper mapper;

    public Users queryOne(Integer id) {
        return mapper.selectByPrimaryKey(id);
    }


    public Users queryOne(String region, String phone) {

        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("region",region);
        criteria.andEqualTo("phone",phone);
        return mapper.selectOneByExample(example);
    }


    public int createUser(Users u) {
        mapper.insertSelective(u);
        return u.getId();
    }


    public void updatePassword(String region, String phone, String hashStr, int salt) {
        Users user = new Users();
        user.setPasswordHash(hashStr);
        user.setPasswordSalt(String.valueOf(salt));
        user.setUpdatedAt(new Date());

        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("region",region);
        criteria.andEqualTo("phone",phone);

        mapper.updateByExampleSelective(user,example);

    }

    public void updateNickName(String nickname, Integer currentUserId) {
        Users users = new Users();
        users.setId(currentUserId);
        users.setNickname(nickname);
        users.setTimestamp(System.currentTimeMillis());
        users.setUpdatedAt(new Date());
        mapper.updateByPrimaryKey(users);
    }

    public void updatePortraitUri(String portraitUri, Integer currentUserId) {

        Users users = new Users();
        users.setId(currentUserId);
        users.setPortraitUri(portraitUri);
        users.setTimestamp(System.currentTimeMillis());
        users.setUpdatedAt(new Date());
        mapper.updateByPrimaryKey(users);
    }

    public void updateToken(String token, Integer id) {
        Users users = new Users();
        users.setId(id);
        users.setRongCloudToken(token);
        users.setTimestamp(System.currentTimeMillis());
        users.setUpdatedAt(new Date());
        mapper.updateByPrimaryKey(users);

    }
}
