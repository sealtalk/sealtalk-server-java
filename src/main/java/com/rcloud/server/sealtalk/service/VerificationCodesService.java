package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.VerificationCodesMapper;
import com.rcloud.server.sealtalk.domain.Users;
import com.rcloud.server.sealtalk.domain.VerificationCodes;

import javax.annotation.Resource;

import io.swagger.models.auth.In;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class VerificationCodesService {

    @Resource
    private VerificationCodesMapper mapper;

    public VerificationCodes queryOne(String region, String phone) {

        Example example = new Example(VerificationCodes.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("region",region);
        criteria.andEqualTo("phone",phone);
        return mapper.selectOneByExample(example);
    }

    public VerificationCodes queryOne(String token) {

        Example example = new Example(VerificationCodes.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("token",token);
        return mapper.selectOneByExample(example);
    }

    public void insert(VerificationCodes verificationCodes) {
        mapper.insertSelective(verificationCodes);
    }

    public void update(VerificationCodes verificationCodes) {
        Assert.notNull(verificationCodes.getId(),"id is null");
        mapper.updateByPrimaryKeySelective(verificationCodes);
    }
}
