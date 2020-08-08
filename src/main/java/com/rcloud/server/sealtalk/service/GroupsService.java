package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.GroupsMapper;
import javax.annotation.Resource;

import com.rcloud.server.sealtalk.domain.Groups;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class GroupsService extends AbstractBaseService<Groups,Integer>{

    @Resource
    private GroupsMapper mapper;

    @Override
    protected Mapper<Groups> getMapper() {
        return mapper;
    }
}
