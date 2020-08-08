package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.VerificationCodesMapper;
import com.rcloud.server.sealtalk.domain.Users;
import com.rcloud.server.sealtalk.domain.VerificationCodes;

import javax.annotation.Resource;

import io.swagger.models.auth.In;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class VerificationCodesService extends AbstractBaseService<VerificationCodes,Integer> {

    @Resource
    private VerificationCodesMapper mapper;

    @Override
    protected Mapper<VerificationCodes> getMapper() {
        return mapper;
    }
}
