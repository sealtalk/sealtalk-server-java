package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.UsersMapper;
import com.rcloud.server.sealtalk.domain.Users;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
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
public class UsersService extends AbstractBaseService<Users,Integer>{

    @Resource
    private UsersMapper mapper;

    @Override
    protected Mapper getMapper() {
        return mapper;
    }

}
