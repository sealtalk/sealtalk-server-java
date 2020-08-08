package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.VerificationViolationsMapper;
import com.rcloud.server.sealtalk.domain.VerificationViolations;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class VerificationViolationsService extends AbstractBaseService<VerificationViolations,String> {

    @Resource
    private VerificationViolationsMapper mapper;

    @Override
    protected Mapper<VerificationViolations> getMapper() {
        return mapper;
    }

    public VerificationViolations queryOne(String ip) {
//        VerificationViolationsExample example = new VerificationViolationsExample()
//            .createCriteria()
//            .andIpEqualTo(ip)
//            .example();
//        return mapper.selectOneByExample(example);
        return null;
    }
}
