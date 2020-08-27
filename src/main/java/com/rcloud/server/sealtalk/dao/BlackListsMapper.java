package com.rcloud.server.sealtalk.dao;

import com.rcloud.server.sealtalk.domain.BlackLists;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BlackListsMapper extends Mapper<BlackLists> {
    List<BlackLists> selectBlackListsWithFriendUsers(@Param("userId") Integer userId);
}