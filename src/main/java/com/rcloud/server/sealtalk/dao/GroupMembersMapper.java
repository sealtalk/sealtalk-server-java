package com.rcloud.server.sealtalk.dao;

import com.rcloud.server.sealtalk.domain.GroupMembers;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

public interface GroupMembersMapper extends Mapper<GroupMembers> {

    List<GroupMembers> queryGroupMembersWithGroupByMemberId(Example example);
}