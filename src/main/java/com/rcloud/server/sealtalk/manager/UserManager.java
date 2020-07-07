package com.rcloud.server.sealtalk.manager;

import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.domain.VerificationCodes;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.model.response.Response;
import com.rcloud.server.sealtalk.service.VerificationCodesService;
import com.rcloud.server.sealtalk.util.RegexUtils;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/7
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class UserManager extends BaseManager {

    private final static String REGION_NUM = "86";

    @Resource
    private VerificationCodesService verificationCodesService;

    public Response sendCode(String region, String phone) throws ServiceException {
        checkRegion(region);
        String completePhone = region + phone;
        checkCompletePhone(completePhone);
        VerificationCodes verificationCodes = verificationCodesService.queryOne(region, phone);

        return new Response("200", StringUtils.EMPTY, "");
    }

    private void checkCompletePhone(String completePhone) throws ServiceException {
        if (!RegexUtils.checkMobile(completePhone)) {
            throw new ServiceException(ErrorCode.REQUEST_ERROR, "Invalid region and phone number.");
        }
    }

    private void checkRegion(String region) throws ServiceException {
        if (!REGION_NUM.equals(region)) {
            throw new ServiceException(ErrorCode.REQUEST_ERROR, "Invalid region and phone number.");
        }
    }
}
