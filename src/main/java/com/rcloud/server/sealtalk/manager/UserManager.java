package com.rcloud.server.sealtalk.manager;

import com.rcloud.server.sealtalk.configuration.ProfileConfig;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.constant.SmsServiceType;
import com.rcloud.server.sealtalk.domain.*;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.model.ServerApiParams;
import com.rcloud.server.sealtalk.rongcloud.RongCloudClient;
import com.rcloud.server.sealtalk.service.*;
import com.rcloud.server.sealtalk.sms.SmsService;
import com.rcloud.server.sealtalk.sms.SmsServiceFactory;
import com.rcloud.server.sealtalk.spi.verifycode.VerifyCodeAuthentication;
import com.rcloud.server.sealtalk.spi.verifycode.VerifyCodeAuthenticationFactory;
import com.rcloud.server.sealtalk.util.*;
import io.rong.models.response.TokenResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @Author: xiuwei.nie
 * @Author: Jianlu.Yu
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

    @Resource
    private GroupMembersService groupMembersService;

    @Resource
    private RongCloudClient rongCloudClient;

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
        VerificationCodes verificationCodes = verificationCodesService.queryOne(region, phone);
        if (verificationCodes == null) {
            verificationCodes = new VerificationCodes();
            verificationCodes.setRegion(region);
            verificationCodes.setPhone(phone);
            verificationCodes.setSessionId(sessionId);
            //默认uuid str
            verificationCodes.setToken(UUID.randomUUID().toString());
            verificationCodes.setCreatedAt(new Date());
            verificationCodes.setUpdatedAt(verificationCodes.getCreatedAt());
            verificationCodesService.insert(verificationCodes);
        } else {
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
        verifyCodeAuthentication.validate(verificationCodes, code, profileConfig.getEnv());
        return verificationCodes.getToken();
    }

    @Transactional(rollbackFor = {Exception.class})
    public long register(String nickname, String password, String verificationToken, HttpServletResponse response) throws ServiceException {

        VerificationCodes verificationCodes = verificationCodesService.queryOne(verificationToken);

        if (verificationCodes == null) {
            throw new ServiceException(ErrorCode.UNKNOWN_VERIFICATION_TOKEN);
        }

        Users users = usersService.queryOne(verificationCodes.getRegion(), verificationCodes.getPhone());

        if (users != null) {
            throw new ServiceException(ErrorCode.PHONE_ALREADY_REGIESTED);
        }
        //如果没有注册过，密码hash
        int salt = RandomUtil.randomBetween(1000, 9999);
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
        int id = usersService.createUser(u);
        //插入DataVersion表
        DataVersions dataVersions = new DataVersions();
        dataVersions.setUserId(u.getId());
        dataVersionsService.createDataVersion(dataVersions);
        //设置cookie
        setCookie(response, id);
        //缓存nickname
        MiscUtils.cacheNickName(u.getId(), u.getNickname());

        //上报管理后台TODO
        return id;
    }

    /**
     * 用户登录
     *
     * @param region
     * @param phone
     * @param password
     */
    public Pair<String, String> login(String region, String phone, String password, HttpServletResponse response) throws ServiceException {

        Users u = usersService.queryOne(region, phone);
        //判断用户是否存在
        if (u == null) {
            throw new ServiceException(ErrorCode.USER_NOT_EXISTER);
        }
        //校验密码是否正确
        String passwordHash = MiscUtils.hash(password, Integer.valueOf(u.getPasswordSalt()));

        if (!passwordHash.equals(u.getPasswordHash())) {
            throw new ServiceException(ErrorCode.USER_PASSWORD_WRONG);
        }

        //设置cookie
        setCookie(response, u.getId());
        //缓存nickname
        MiscUtils.cacheNickName(u.getId(), u.getNickname());

        //查询该用户所属的所有组,同步到融云
        Map<String, String> groupIdNameMap = new HashMap<>();
        List<GroupMembers> groupMembersList = groupMembersService.queryGroupMembersWithGroupByMemberId(u.getId());
        if (!CollectionUtils.isEmpty(groupMembersList)) {
            for (GroupMembers gm : groupMembersList) {
                Groups groups = gm.getGroups();
                if (groups != null) {
                    groupIdNameMap.put(N3d.encode(groups.getId()), groups.getName());
                }
            }
        }

        //同步前记录日志
        log.info("'Sync groups: {}", groupIdNameMap);
        //调用融云sdk 将登录用户的userid，与groupIdName信息同步到融云 TODO


        String token = u.getRongCloudToken();
        if (StringUtils.isEmpty(token)) {
            //如果user表中的融云token为空，
            //调用融云sdk 获取token
            TokenResult tokenResult = rongCloudClient.register(u.getId(), u.getNickname(), u.getPortraitUri());
            token = tokenResult.getToken();
            //获取后根据userId更新表中token
            usersService.updateToken(token,u.getId());
        }

        //返回userid、token
        return Pair.of(String.valueOf(u.getId()), token);
    }

    private void setCookie(HttpServletResponse response, int userId) {
        byte[] value = AES256.encrypt(String.valueOf(userId), sealtalkConfig.getAuthCookieKey());
        Cookie cookie = new Cookie(sealtalkConfig.getAuthCookieName(), new String(value));
        cookie.setHttpOnly(true);
        cookie.setDomain(sealtalkConfig.getAuthCookieDomain());
        cookie.setMaxAge(Integer.valueOf(sealtalkConfig.getAuthCookieMaxAge()));
        response.addCookie(cookie);
    }

    public void resetPassword(String password, String verificationToken) throws ServiceException {
        VerificationCodes verificationCodes = verificationCodesService.queryOne(verificationToken);
        if (verificationCodes == null) {
            throw new ServiceException(ErrorCode.UNKNOWN_VERIFICATION_TOKEN);
        }

        //新密码hash,修改user表密码字段
        int salt = RandomUtil.randomBetween(1000, 9999);
        String hashStr = MiscUtils.hash(password, salt);
        usersService.updatePassword(verificationCodes.getRegion(), verificationCodes.getPhone(), hashStr, salt);
    }

    public void changePassword(String newPassword, String oldPassword, Integer currentUserId) throws ServiceException {

        Users u = usersService.queryOne(currentUserId);

        if (u == null) {
            //TODO  未确认的错误
            throw new ServiceException(ErrorCode.REQUEST_ERROR);
        }

        String oldPasswordHash = MiscUtils.hash(oldPassword, Integer.valueOf(u.getPasswordSalt()));

        if (!oldPasswordHash.equals(u.getPasswordHash())) {
            throw new ServiceException(ErrorCode.USER_PASSWORD_WRONG_2);
        }

        //新密码hash,修改user表密码字段
        int salt = RandomUtil.randomBetween(1000, 9999);
        String hashStr = MiscUtils.hash(newPassword, salt);
        usersService.updatePassword(u.getRegion(), u.getPhone(), hashStr, salt);
    }

    public void setNickName(String nickname, Integer currentUserId) throws ServiceException {
        //修改昵称
        usersService.updateNickName(nickname, currentUserId);

        //调用融云刷新用户信息
        rongCloudClient.updateUser(currentUserId, nickname, null);

        //缓存用户昵称
        MiscUtils.cacheNickName(currentUserId, nickname);

        //修改DataVersion表中 UserVersion

        //修改DataVersion表中 AllFriendshipVersion

        //清除缓存"user_" + currentUserId

        //清除缓存"friendship_profile_user_" + currentUserId

        //查询该用户所有好友关系

        //循环清除缓存"friendship_all_" + friend.friendId

        //查询该用户所属组groupid isDeleted: false

        ///循环清除缓存"group_members_" + groupMember.groupId

        return;

    }

    public void setPortraitUri(String portraitUri, Integer currentUserId) throws ServiceException {

        //修改头像
        usersService.updatePortraitUri(portraitUri, currentUserId);

        //调用融云刷新用户信息
        rongCloudClient.updateUser(currentUserId, null, portraitUri);

        //修改DataVersion表中 UserVersion

        //修改DataVersion表中 AllFriendshipVersion

        //清除缓存"user_" + currentUserId

        //清除缓存"friendship_profile_user_" + currentUserId

        //查询该用户所有好友关系

        //循环清除缓存"friendship_all_" + friend.friendId

        //查询该用户所属组groupid isDeleted: false

        ///循环清除缓存"group_members_" + groupMember.groupId

        return;
    }


    public Pair<String,String> getToken(Integer currentUserId) throws ServiceException {

        Users user = usersService.queryOne(currentUserId);

        TokenResult tokenResult = rongCloudClient.register(user.getId(), user.getNickname(), user.getPortraitUri());
        String token = tokenResult.getToken();
        //获取后根据userId更新表中token
        usersService.updateToken(token,user.getId());
        return Pair.of(String.valueOf(user.getId()),token);

    }
}
