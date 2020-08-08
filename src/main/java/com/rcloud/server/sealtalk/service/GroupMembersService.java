package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.GroupMembersMapper;
import com.rcloud.server.sealtalk.domain.GroupMembers;
import org.springframework.stereotype.Service;
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
public class GroupMembersService extends AbstractBaseService<GroupMembers,Integer>{

    @Resource
    private GroupMembersMapper mapper;

    @Override
    protected Mapper<GroupMembers> getMapper() {
        return mapper;
    }


    /**
     * 根据memberId查询用户所属组
     *
     * @param memberId 用户id
     * @return
     */
    public List<GroupMembers> queryGroupMembersWithGroupByMemberId(int memberId){
        Example example = new Example(GroupMembers.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("memberId",memberId);
        return mapper.queryGroupMembersWithGroupByMemberId(example);
    }


}
