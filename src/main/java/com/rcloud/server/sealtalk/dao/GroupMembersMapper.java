package com.rcloud.server.sealtalk.dao;

import com.rcloud.server.sealtalk.domain.GroupMembers;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

public interface GroupMembersMapper extends Mapper<GroupMembers> {

    List<GroupMembers> queryGroupMembersWithGroupByMemberId(@Param("memberId") Integer memberId);

    List<GroupMembers> queryGroupMembersWithUsersByGroupId(@Param("groupId") Integer groupId,@Param("isDeleted") Integer isDeleted);

    GroupMembers queryGroupMembersWithGroupByGroupIdAndMemberId(@Param("groupId") Integer groupId, @Param("memberId") Integer memberId);

    List<GroupMembers> selectGroupMembersWithUsersByGroupIdsAndVersion(@Param("groupIdList") List<Integer> groupIdList, @Param("version") Long version);

    int insertBatch(@Param("groupMemberList") List<GroupMembers > groupMemberList);
}