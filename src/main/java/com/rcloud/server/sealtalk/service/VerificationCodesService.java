package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.VerificationCodesMapper;
import com.rcloud.server.sealtalk.domain.VerificationCodes;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

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

    public VerificationCodes saveOrUpdate(String region, String phone, String sessionId) {

        Example example = new Example(VerificationCodes.class);
        example.createCriteria().andEqualTo("region",region)
                .andEqualTo("phone",phone);

        VerificationCodes verificationCodes = this.getOneByExample(example);

        if (verificationCodes == null) {
            verificationCodes = new VerificationCodes();
            verificationCodes.setRegion(region);
            verificationCodes.setPhone(phone);
            verificationCodes.setSessionId(sessionId);
            //默认uuid1 str
            verificationCodes.setToken(UUID.randomUUID().toString());
            verificationCodes.setCreatedAt(new Date());
            verificationCodes.setUpdatedAt(verificationCodes.getCreatedAt());
            this.saveSelective(verificationCodes);
        } else {
            VerificationCodes newVerificationCodes = new VerificationCodes();
            newVerificationCodes.setRegion(region);
            newVerificationCodes.setPhone(phone);
            newVerificationCodes.setSessionId(sessionId);
            newVerificationCodes.setUpdatedAt(new Date());
            newVerificationCodes.setId(verificationCodes.getId());
            this.updateByPrimaryKeySelective(newVerificationCodes);
        }
        return verificationCodes;
    }

    public VerificationCodes getByRegionAndPhone(String region, String phone) {
        Example example = new Example(VerificationCodes.class);
        example.createCriteria().andEqualTo("region",region).andEqualTo("phone",phone);
        return this.getOneByExample(example);
    }

    public VerificationCodes getByToken(String verificationToken) {

        VerificationCodes v = new VerificationCodes();
        v.setToken(verificationToken);
        return this.getOne(v);

    }
}
