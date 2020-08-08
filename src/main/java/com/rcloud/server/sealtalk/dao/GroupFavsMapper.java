package com.rcloud.server.sealtalk.dao;

import com.rcloud.server.sealtalk.domain.GroupFavs;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface GroupFavsMapper extends Mapper<GroupFavs> {

    List<GroupFavs> queryGroupFavsWithGroupByUserId(@Param("userId") Integer userId, @Param("limit") Integer limit, @Param("offset") Integer offset);
}