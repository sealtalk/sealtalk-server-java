package com.rcloud.server.sealtalk.dao;

import com.rcloud.server.sealtalk.domain.GroupBulletins;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface GroupBulletinsMapper extends Mapper<GroupBulletins> {
    GroupBulletins getLastestGroupBulletin(@Param("groupId") Integer groupId);
}