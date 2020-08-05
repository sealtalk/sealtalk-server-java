package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.UsersMapper;
import com.rcloud.server.sealtalk.domain.Users;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;

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

    public Users queryOne(Integer friendId) {
        return mapper.selectByPrimaryKey(friendId);
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
}
