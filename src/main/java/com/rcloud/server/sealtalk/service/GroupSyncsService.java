package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.GroupSyncsMapper;
import javax.annotation.Resource;

import com.rcloud.server.sealtalk.domain.GroupSyncs;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class GroupSyncsService extends AbstractBaseService<GroupSyncs,Integer>{

    @Resource
    private GroupSyncsMapper mapper;

    @Override
    protected Mapper<GroupSyncs> getMapper() {
        return mapper;
    }
}
