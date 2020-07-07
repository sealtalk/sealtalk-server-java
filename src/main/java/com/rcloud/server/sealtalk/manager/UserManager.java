package com.rcloud.server.sealtalk.manager;

import com.fasterxml.jackson.databind.JsonNode;
import com.rcloud.server.sealtalk.configuration.ProfileConfig;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.domain.VerificationCodes;
import com.rcloud.server.sealtalk.domain.VerificationViolations;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.model.ServerApiParams;
import com.rcloud.server.sealtalk.service.VerificationCodesService;
import com.rcloud.server.sealtalk.service.VerificationViolationsService;
import com.rcloud.server.sealtalk.util.JacksonUtil;
import com.rcloud.server.sealtalk.util.RegexUtils;
import io.rong.RongCloud;
import java.io.IOException;
import java.util.Date;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/7
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
@Slf4j
public class UserManager extends BaseManager {

    @Resource
    private ProfileConfig profileConfig;

    @Resource
    private VerificationCodesService verificationCodesService;

    @Resource
    private VerificationViolationsService verificationViolationsService;

    /**
     * 向手机发送验证码
     */
    public void sendCode(String region, String phone) throws ServiceException {
        log.info("send code. region:[{}] phone:[{}]", region, phone);
        checkRegion(region);
        String completePhone = region + phone;
        checkCompletePhone(completePhone);
        VerificationCodes verificationCodes = verificationCodesService.queryOne(region, phone);
        if (verificationCodes != null) {
            Date limitDate = getLimitDate();
            checkLimitDate(limitDate, verificationCodes);
        }
        upsertAndSendToSms(region, phone);
    }

    /**
     * 发送短信并更新数据库
     */
    private void upsertAndSendToSms(String region, String phone) throws ServiceException {
        if (Constants.ENV_DEV.equals(profileConfig.getEnv())) {
            upsert(region, phone, "");
        } else if (!"".equals(sealtalkConfig.getRongcloudSmsRegisterTemplateId())) {
            String result = sendToSms(region, phone);
            try {
                JsonNode jsonNode = JacksonUtil.getJsonNode(result);
                int code = jsonNode.get("code").asInt();
                String sessionId = jsonNode.get("sessionId").asText();
                if (Constants.HTTP_SUCCESS_CODE != code) {
                    log.error("RongCloud Server API Error. Code:[{}]", code);
                    throw new ServiceException(ErrorCode.SEND_SMS_ERROR,
                        "RongCloud Server API Error Code: " + code);
                }
                upsert(region, phone, sessionId);
            } catch (IOException e) {
                log.error("Send sms result to json error.", e);
                throw new ServiceException(ErrorCode.SERVER_ERROR);
            }
        }
    }

    /**
     * 发送短信
     */
    private String sendToSms(String region, String phone) {
        RongCloud rongCloud = RongCloud.getInstance(sealtalkConfig.getRongcloudAppKey(),
            sealtalkConfig.getRongcloudAppSecret());
        // todo send sms

        return "";
    }

    /**
     * 添加更新数据库
     */
    private void upsert(String region, String phone, String sessionId) {
        verificationCodesService.upsert(region, phone, sessionId);
    }

    private void checkLimitDate(Date limitDate, VerificationCodes verificationCodes)
        throws ServiceException {
        long updatedTime = verificationCodes.getUpdatedAt().getTime();
        long limitDateTime = limitDate.getTime();
        if (limitDateTime < updatedTime) {
            throw new ServiceException(ErrorCode.LIMIT_ERROR);
        }
    }

    private Date getLimitDate() {
        DateTime dateTime = new DateTime(new Date());
        if (Constants.ENV_DEV.equals(profileConfig.getEnv())) {
            dateTime.minusSeconds(5);
        } else {
            dateTime.minusMinutes(1);
        }
        return dateTime.toDate();
    }

    private void checkCompletePhone(String completePhone) throws ServiceException {
        if (!RegexUtils.checkMobile(completePhone)) {
            throw new ServiceException(ErrorCode.REQUEST_ERROR, "Invalid region and phone number.");
        }
    }

    private void checkRegion(String region) throws ServiceException {
        if (!Constants.REGION_NUM.equals(region)) {
            throw new ServiceException(ErrorCode.REQUEST_ERROR, "Invalid region and phone number.");
        }
    }

    /**
     * 向手机发送验证码(云片服务)
     */
    public void sendCodeYp(String region, String phone, ServerApiParams serverApiParams)
        throws ServiceException {
        log.info("send code yp. region:[{}] phone:[{}]", region, phone);
        region = removeRegionPrefix(region);
        VerificationCodes verificationCodes = verificationCodesService.queryOne(region, phone);
        if (verificationCodes != null) {
            Date limitDate = getLimitDate();
            checkLimitDate(limitDate, verificationCodes);
        }
        upsertAndSendToSmsYp(region, phone, serverApiParams);
    }

    private void upsertAndSendToSmsYp(String region, String phone, ServerApiParams serverApiParams)
        throws ServiceException {
        if (Constants.ENV_DEV.equals(profileConfig.getEnv())) {
            upsert(region, phone, "");
        } else {
            check(serverApiParams);
            String result = sendYunPianCode(region, phone);
            upsert(region, phone, "");
        }
    }

    private String sendYunPianCode(String region, String phone) {

        return "";
    }

    private void check(ServerApiParams serverApiParams) throws ServiceException {
        String ip = serverApiParams.getRequestUriInfo().getIp();
        VerificationViolations verificationViolations = verificationViolationsService.queryOne(ip);
        if (verificationViolations == null) {
            verificationViolations = new VerificationViolations();
            verificationViolations.setTime(new Date());
            verificationViolations.setCount(0);
        }
        Integer yunpianLimitedTime = sealtalkConfig.getYunpianLimitedTime();
        Integer yunpianLimitedCount = sealtalkConfig.getYunpianLimitedCount();
        DateTime dateTime = new DateTime(new Date());
        Date sendDate = dateTime.minusHours(yunpianLimitedTime).toDate();
        boolean beyondLimit = verificationViolations.getCount() >= yunpianLimitedCount;
        if (sendDate.getTime() < verificationViolations.getTime().getTime() && beyondLimit) {
            throw new ServiceException(ErrorCode.YUN_PIAN_SMS_ERROR);
        }
    }

    private String removeRegionPrefix(String region) {
        if (region.startsWith(Constants.STRING_ADD)) {
            region = region.substring(1);
        }
        return region;
    }
}
