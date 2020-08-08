package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.ScreenStatusesMapper;
import javax.annotation.Resource;

import com.rcloud.server.sealtalk.domain.ScreenStatuses;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class ScreenStatusesService extends AbstractBaseService<ScreenStatuses,Integer>{

    @Resource
    private ScreenStatusesMapper mapper;

    @Override
    protected Mapper<ScreenStatuses> getMapper() {
        return mapper;
    }
}
