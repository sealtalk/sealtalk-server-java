package com.rcloud.server.sealtalk.service;

import com.google.gson.internal.$Gson$Preconditions;
import com.rcloud.server.sealtalk.dao.UsersMapper;
import com.rcloud.server.sealtalk.domain.Users;
import com.rcloud.server.sealtalk.domain.VerificationCodes;
import com.rcloud.server.sealtalk.util.CacheUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class UsersService extends AbstractBaseService<Users, Integer> {

    @Resource
    private UsersMapper mapper;

    @Override
    protected Mapper getMapper() {
        return mapper;
    }

    public String getCurrentUserNickNameWithCache(Integer currentUserId) {

        String nickName = CacheUtil.get(CacheUtil.NICK_NAME_CACHE_PREFIX+currentUserId);
        if(StringUtils.isEmpty(nickName)){
            Users users = mapper.selectByPrimaryKey(currentUserId);
            if(users!=null){
                nickName = users.getNickname();
                CacheUtil.set(CacheUtil.NICK_NAME_CACHE_PREFIX+currentUserId,nickName);
            }
        }
        return nickName;
    }

    public List<Users> getUsers(List<Integer> ids) {
        Example example = new Example(Users.class);
        example.createCriteria().andIn("id",ids);
        return this.getByExample(example);

    }



}
