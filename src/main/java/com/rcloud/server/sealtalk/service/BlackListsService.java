package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.BlackListsMapper;
import com.rcloud.server.sealtalk.domain.BlackLists;
import org.apache.ibatis.session.RowBounds;
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
public class BlackListsService extends AbstractBaseService<BlackLists, Integer> {

    @Resource
    private BlackListsMapper mapper;

    @Override
    protected Mapper<BlackLists> getMapper() {
        return mapper;
    }


    public void saveOrUpdate(Integer currentUserId, Integer friendId, boolean status, long currentTimeMillis) {
        //TODO

//        mapper.existsWithPrimaryKey()
//                mapper.selectByRowBounds(RowBounds)
//                        mapper.
    }


}
