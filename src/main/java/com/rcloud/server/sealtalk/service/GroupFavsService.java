package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.GroupFavsMapper;
import com.rcloud.server.sealtalk.domain.GroupFavs;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class GroupFavsService extends AbstractBaseService<GroupFavs, Integer> {

    @Resource
    private GroupFavsMapper mapper;

    @Override
    protected Mapper<GroupFavs> getMapper() {
        return mapper;
    }

    public List<GroupFavs> queryGroupFavsWithGroupByUserId(Integer userId, Integer limit, Integer offset) {
        return mapper.queryGroupFavsWithGroupByUserId(userId, limit, offset);
    }
}
