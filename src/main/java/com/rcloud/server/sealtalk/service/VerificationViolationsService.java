package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.VerificationViolationsMapper;
import com.rcloud.server.sealtalk.domain.VerificationViolations;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import javax.annotation.Resource;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class VerificationViolationsService extends AbstractBaseService<VerificationViolations, String> {

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
