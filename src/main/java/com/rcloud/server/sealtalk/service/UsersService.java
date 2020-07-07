package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.UsersMapper;
import com.rcloud.server.sealtalk.domain.Users;
import com.rcloud.server.sealtalk.domain.UsersExample;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

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
        UsersExample example = new UsersExample()
            .createCriteria()
            .andIdEqualTo(friendId)
            .example();
        return mapper.selectOneByExample(example);
    }
}
