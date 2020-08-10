package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.GroupBulletinsMapper;
import com.rcloud.server.sealtalk.domain.GroupBulletins;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import javax.annotation.Resource;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class GroupBulletinsService extends AbstractBaseService<GroupBulletins, Integer> {

    @Resource
    private GroupBulletinsMapper mapper;

    @Override
    protected Mapper<GroupBulletins> getMapper() {
        return mapper;
    }
}
