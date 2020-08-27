package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.GroupFavsMapper;
import com.rcloud.server.sealtalk.domain.GroupFavs;
import com.rcloud.server.sealtalk.util.ValidateUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

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

    public void deleteByGroupIdAndUserId(Integer groupId, List<Integer> userIdList) {
        Assert.notNull(groupId,"groupId is null");

        Example example = new Example(GroupFavs.class);

        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("groupId", groupId);
        if(!CollectionUtils.isEmpty(userIdList)){
            criteria.andIn("userId", userIdList);
        }
        this.deleteByExample(example);
    }


    public Integer queryCountGroupFavs(Integer userId){
        return mapper.queryCountGroupFavsWithGroupByUserId(userId);
    }
}
