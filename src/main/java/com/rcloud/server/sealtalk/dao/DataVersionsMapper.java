package com.rcloud.server.sealtalk.dao;

import com.rcloud.server.sealtalk.domain.DataVersions;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface DataVersionsMapper extends Mapper<DataVersions> {

    void updateAllFriendshipVersion(@Param("userId") int userId,@Param("timestamp") long timestamp);

}