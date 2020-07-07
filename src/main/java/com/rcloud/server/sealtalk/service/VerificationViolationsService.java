package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.VerificationViolationsMapper;
import com.rcloud.server.sealtalk.domain.VerificationCodes;
import com.rcloud.server.sealtalk.domain.VerificationCodesExample;
import com.rcloud.server.sealtalk.domain.VerificationViolations;
import com.rcloud.server.sealtalk.domain.VerificationViolationsExample;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class VerificationViolationsService {

    @Resource
    private VerificationViolationsMapper mapper;

    public VerificationViolations queryOne(String ip) {
        VerificationViolationsExample example = new VerificationViolationsExample()
            .createCriteria()
            .andIpEqualTo(ip)
            .example();
        return mapper.selectOneByExample(example);
    }
}
