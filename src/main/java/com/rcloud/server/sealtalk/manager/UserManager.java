package com.rcloud.server.sealtalk.manager;

import com.rcloud.server.sealtalk.configuration.ProfileConfig;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.constant.SmsServiceType;
import com.rcloud.server.sealtalk.domain.DataVersions;
import com.rcloud.server.sealtalk.domain.Users;
import com.rcloud.server.sealtalk.domain.VerificationCodes;
import com.rcloud.server.sealtalk.domain.VerificationViolations;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.model.ServerApiParams;
import com.rcloud.server.sealtalk.service.DataVersionsService;
import com.rcloud.server.sealtalk.service.UsersService;
import com.rcloud.server.sealtalk.service.VerificationCodesService;
import com.rcloud.server.sealtalk.service.VerificationViolationsService;
import com.rcloud.server.sealtalk.sms.SmsService;
import com.rcloud.server.sealtalk.sms.SmsServiceFactory;
import com.rcloud.server.sealtalk.spi.verifycode.VerifyCodeAuthentication;
import com.rcloud.server.sealtalk.spi.verifycode.VerifyCodeAuthenticationFactory;
import com.rcloud.server.sealtalk.util.MiscUtils;
import com.rcloud.server.sealtalk.util.RandomUtil;
import com.rcloud.server.sealtalk.util.ValidateUtils;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

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

    @Resource
    private UsersService usersService;

    @Resource
    private DataVersionsService dataVersionsService;

    /**
     * 向手机发送验证码
     */
    public void sendCode(String region, String phone, SmsServiceType smsServiceType, ServerApiParams serverApiParams) throws ServiceException {
        log.info("send code. region:[{}] phone:[{}] smsServiceType:[{}]", region, phone, smsServiceType.getCode());
        //如果是开发环境，且是调用云片服务直接返回，不执行后续逻辑
        if (Constants.ENV_DEV.equals(profileConfig.getEnv()) && SmsServiceType.YUNPIAN.equals(smsServiceType)) {
            return;
        }
        region = MiscUtils.removeRegionPrefix(region);
        ValidateUtils.checkRegion(region);
        String completePhone = region + phone;
        ValidateUtils.checkCompletePhone(phone);
        VerificationCodes verificationCodes = verificationCodesService.queryOne(region, phone);
        if (verificationCodes != null) {
            Date limitDate = getLimitDate();
            checkLimitDate(limitDate, verificationCodes);
        }
        if (SmsServiceType.YUNPIAN.equals(smsServiceType)) {
            check(serverApiParams);
        }

        upsertAndSendToSms(region, phone, smsServiceType);
    }

    /**
     * 发送短信并更新数据库
     */
    private void upsertAndSendToSms(String region, String phone, SmsServiceType smsServiceType) throws ServiceException {
        if (Constants.ENV_DEV.equals(profileConfig.getEnv())) {
            //开发环境直接插入数据库，不调用短信接口
            saveOrUpdate(region, phone, "");
        } else {
            SmsService smsService = SmsServiceFactory.getSmsService(smsServiceType);
            String sessionId = smsService.sendVerificationCode(region, phone);
            saveOrUpdate(region, phone, sessionId);
        }
    }

    /**
     * 验证码添加或更新数据库
     */
    private void saveOrUpdate(String region, String phone, String sessionId) {
        VerificationCodes verificationCodes = verificationCodesService.queryOne(region,phone);
        if(verificationCodes==null){
            verificationCodes = new VerificationCodes();
            verificationCodes.setRegion(region);
            verificationCodes.setPhone(phone);
            verificationCodes.setSessionId(sessionId);
            verificationCodes.setToken("");
            verificationCodes.setCreatedAt(new Date());
            verificationCodes.setUpdatedAt(verificationCodes.getCreatedAt());
            verificationCodesService.insert(verificationCodes);
        }else {
            verificationCodes.setRegion(region);
            verificationCodes.setPhone(phone);
            verificationCodes.setSessionId(sessionId);
            verificationCodes.setUpdatedAt(verificationCodes.getCreatedAt());
            verificationCodesService.update(verificationCodes);
        }
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


    /**
     * 判断用户是否已经存在
     *
     * @param region
     * @param phone
     * @return
     * @throws ServiceException
     */
    public boolean isExistUser(String region, String phone) throws ServiceException {

        ValidateUtils.checkRegion(region);
        ValidateUtils.checkCompletePhone(phone);
        Users users = usersService.queryOne(region, phone);
        return !(users == null);
    }


    /**
     * 校验验证码
     *
     * @param region
     * @param phone
     * @param code
     * @param smsServiceType
     * @return
     * @throws ServiceException
     */
    public String verifyCode(String region, String phone, String code, SmsServiceType smsServiceType) throws ServiceException {
        VerificationCodes verificationCodes = verificationCodesService.queryOne(region, phone);
        VerifyCodeAuthentication verifyCodeAuthentication = VerifyCodeAuthenticationFactory.getVerifyCodeAuthentication(smsServiceType);
        verifyCodeAuthentication.validate(verificationCodes,code,profileConfig.getEnv());
        return verificationCodes.getToken();
    }

    @Transactional(rollbackFor = {Exception.class})
    public long register(String nickname, String password, String verificationToken) throws ServiceException{

        VerificationCodes verificationCodes = verificationCodesService.queryOne(verificationToken);

        if(verificationCodes==null){
            throw new ServiceException(ErrorCode.UNKNOWN_VERIFICATION_TOKEN);
        }

        Users users = usersService.queryOne(verificationCodes.getRegion(), verificationCodes.getPhone());

        if(users!=null){
            throw new ServiceException(ErrorCode.PHONE_ALREADY_REGIESTED);
        }
        //如果没有注册过，密码hash
        int salt = RandomUtil.randomBetween(1000,9999);
        String hashStr = MiscUtils.hash(password, salt);

        //插入user表
        Users u = new Users();
        u.setNickname(nickname);
        u.setRegion(verificationCodes.getRegion());
        u.setPhone(verificationCodes.getPhone());
        u.setPasswordHash(hashStr);
        u.setPasswordSalt(String.valueOf(salt));
        u.setCreatedAt(new Date());
        u.setUpdatedAt(u.getCreatedAt());
        long id = usersService.createUser(u);
        //插入DataVersion表
        DataVersions dataVersions = new DataVersions();
        dataVersions.setUserId(u.getId());
        dataVersionsService.createDataVersion(dataVersions);
        //设置cookie

        //缓存nickName

        //上报管理后台TODO
        return id;
    }
}
