package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.VerificationCodesMapper;
import com.rcloud.server.sealtalk.domain.VerificationCodes;
import com.rcloud.server.sealtalk.domain.VerificationCodesExample;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

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
        VerificationCodesExample example = new VerificationCodesExample()
            .createCriteria()
            .andRegionEqualTo(region)
            .andPhoneEqualTo(phone)
            .example();
        return mapper.selectOneByExample(example);
    }
}
