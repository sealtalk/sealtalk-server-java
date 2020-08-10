package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.VerificationCodesMapper;
import com.rcloud.server.sealtalk.domain.VerificationCodes;
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
public class VerificationCodesService extends AbstractBaseService<VerificationCodes, Integer> {

    @Resource
    private VerificationCodesMapper mapper;

    @Override
    protected Mapper<VerificationCodes> getMapper() {
        return mapper;
    }
}
