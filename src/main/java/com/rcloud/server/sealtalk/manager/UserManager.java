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
import io.rong.models.response.BlackListResult;
import io.rong.models.response.TokenResult;
import io.rong.models.user.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

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
    private RongCloudClient rongCloudClient;

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
    private FriendshipsService friendshipsService;

    @Resource
    private BlackListsService blackListsService;

    @Resource
    private GroupFavsService groupFavsService;

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
        VerificationCodes v = new VerificationCodes();
        v.setRegion(region);
        v.setPhone(phone);
        VerificationCodes verificationCodes = verificationCodesService.getOne(v);
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
        VerificationCodes v = new VerificationCodes();
        v.setRegion(region);
        v.setPhone(phone);

        VerificationCodes verificationCodes = verificationCodesService.getOne(v);

        if (verificationCodes == null) {
            verificationCodes = new VerificationCodes();
            verificationCodes.setRegion(region);
            verificationCodes.setPhone(phone);
            verificationCodes.setSessionId(sessionId);
            //默认uuid str
            verificationCodes.setToken(UUID.randomUUID().toString());
            verificationCodes.setCreatedAt(new Date());
            verificationCodes.setUpdatedAt(verificationCodes.getCreatedAt());
            verificationCodesService.saveSelective(verificationCodes);
        } else {
            verificationCodes.setRegion(region);
            verificationCodes.setPhone(phone);
            verificationCodes.setSessionId(sessionId);
            verificationCodes.setUpdatedAt(verificationCodes.getCreatedAt());
            verificationCodesService.updateByPrimaryKeySelective(verificationCodes);
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
        Users param = new Users();
        param.setRegion(region);
        param.setPhone(phone);
        Users users = usersService.getOne(param);
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

        VerificationCodes v = new VerificationCodes();
        v.setRegion(region);
        v.setPhone(phone);
        VerificationCodes verificationCodes = verificationCodesService.getOne(v);
        VerifyCodeAuthentication verifyCodeAuthentication = VerifyCodeAuthenticationFactory.getVerifyCodeAuthentication(smsServiceType);
        verifyCodeAuthentication.validate(verificationCodes, code, profileConfig.getEnv());
        return verificationCodes.getToken();
    }

    @Transactional(rollbackFor = {Exception.class})
    public long register(String nickname, String password, String verificationToken, HttpServletResponse response) throws ServiceException {

        VerificationCodes v = new VerificationCodes();
        v.setToken(verificationToken);
        VerificationCodes verificationCodes = verificationCodesService.getOne(v);

        if (verificationCodes == null) {
            throw new ServiceException(ErrorCode.UNKNOWN_VERIFICATION_TOKEN);
        }

        Users param = new Users();
        param.setRegion(verificationCodes.getRegion());
        param.setPhone(verificationCodes.getPhone());
        Users users = usersService.getOne(param);

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
        usersService.saveSelective(u);

        int id = u.getId();

        //插入DataVersion表
        DataVersions dataVersions = new DataVersions();
        dataVersions.setUserId(u.getId());
        dataVersionsService.saveSelective(dataVersions);
        //设置cookie
        setCookie(response, id);
        //缓存nickname
        CacheUtil.set(CacheUtil.NICK_NAME_CACHE_PREFIX + u.getId(), u.getNickname());

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


        Users param = new Users();
        param.setRegion(region);
        param.setPhone(phone);
        Users u = usersService.getOne(param);
        //判断用户是否存在
        if (u == null) {
            throw new ServiceException(ErrorCode.USER_NOT_EXIST);
        }
        //校验密码是否正确
        String passwordHash = MiscUtils.hash(password, Integer.valueOf(u.getPasswordSalt()));

        if (!passwordHash.equals(u.getPasswordHash())) {
            throw new ServiceException(ErrorCode.USER_PASSWORD_WRONG);
        }

        //设置cookie
        setCookie(response, u.getId());
        //缓存nickname
        CacheUtil.set(CacheUtil.NICK_NAME_CACHE_PREFIX + u.getId(), u.getNickname());

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
            TokenResult tokenResult = rongCloudClient.register(N3d.encode(u.getId()), u.getNickname(), u.getPortraitUri());
            token = tokenResult.getToken();

            //获取后根据userId更新表中token
            Users users = new Users();
            users.setId(u.getId());
            users.setRongCloudToken(token);
            users.setTimestamp(System.currentTimeMillis());
            users.setUpdatedAt(new Date());
            usersService.updateByPrimaryKeySelective(users);
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
        VerificationCodes v = new VerificationCodes();
        v.setToken(verificationToken);
        VerificationCodes verificationCodes = verificationCodesService.getOne(v);

        if (verificationCodes == null) {
            throw new ServiceException(ErrorCode.UNKNOWN_VERIFICATION_TOKEN);
        }

        //新密码hash,修改user表密码字段
        int salt = RandomUtil.randomBetween(1000, 9999);
        String hashStr = MiscUtils.hash(password, salt);

        updatePassword(verificationCodes.getRegion(), verificationCodes.getPhone(), salt, hashStr);
    }

    public void changePassword(String newPassword, String oldPassword, Integer currentUserId) throws ServiceException {

        Users u = usersService.getByPrimaryKey(currentUserId);

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


        updatePassword(u.getRegion(), u.getPhone(), salt, hashStr);
    }

    private void updatePassword(String region, String phone, int salt, String hashStr) {
        Users user = new Users();
        user.setPasswordHash(hashStr);
        user.setPasswordSalt(String.valueOf(salt));
        user.setUpdatedAt(new Date());

        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("region", region);
        criteria.andEqualTo("phone", phone);
        usersService.updateByExampleSelective(user, example);
    }

    public void setNickName(String nickname, Integer currentUserId) throws ServiceException {
        //修改昵称
        Users users = new Users();
        users.setId(currentUserId);
        users.setNickname(nickname);
        users.setTimestamp(System.currentTimeMillis());
        users.setUpdatedAt(new Date());
        usersService.updateByPrimaryKeySelective(users);

        //调用融云刷新用户信息
        rongCloudClient.updateUser(N3d.encode(currentUserId), nickname, null);

        //缓存用户昵称
        CacheUtil.set(CacheUtil.NICK_NAME_CACHE_PREFIX + currentUserId, nickname);

        //清空缓存、更新版本
        clearCacheAndUpdateVersion(currentUserId);

        return;
    }

    public void setPortraitUri(String portraitUri, Integer currentUserId) throws ServiceException {

        //修改头像
        Users users = new Users();
        users.setId(currentUserId);
        users.setPortraitUri(portraitUri);
        users.setTimestamp(System.currentTimeMillis());
        users.setUpdatedAt(new Date());
        usersService.updateByPrimaryKeySelective(users);

        //调用融云刷新用户信息
        rongCloudClient.updateUser(N3d.encode(currentUserId), null, portraitUri);

        //清空缓存、更新版本
        clearCacheAndUpdateVersion(currentUserId);
        return;
    }

    /**
     * 清除user相关缓存并更新dataversion版本
     *
     * @param currentUserId
     */
    private void clearCacheAndUpdateVersion(Integer currentUserId) {
        //修改DataVersion表中 UserVersion
        long now = System.currentTimeMillis();
        DataVersions dataVersions = new DataVersions();
        dataVersions.setUserId(currentUserId);
        dataVersions.setUserVersion(now);
        dataVersionsService.updateByPrimaryKeySelective(dataVersions);

        //修改DataVersion表中 AllFriendshipVersion
        dataVersionsService.updateAllFriendshipVersion(currentUserId);

        //清除缓存"user_" + currentUserId
        //清除缓存"friendship_profile_user_" + currentUserId
        CacheUtil.delete(CacheUtil.USER_CACHE_PREFIX + currentUserId);
        CacheUtil.delete(CacheUtil.FRIENDSHIP_PROFILE_USER_CACHE_PREFIX + currentUserId);

        //查询该用户所有好友关系,并清除缓存friendship_all_+friendId
        Friendships f = new Friendships();
        f.setUserId(currentUserId);
        List<Friendships> friendshipsList = friendshipsService.get(f);
        if (!CollectionUtils.isEmpty(friendshipsList)) {
            for (Friendships friendships : friendshipsList) {
                CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + friendships.getFriendId());
            }
        }
        //查询该用户所属组groupid isDeleted: false
        //清空缓存group_members_
        Example example = new Example(GroupMembers.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("memberId", currentUserId);
        criteria.andEqualTo("isDeleted", "0");

        List<GroupMembers> groupMembersList = groupMembersService.getByExample(example);
        if (!CollectionUtils.isEmpty(groupMembersList)) {
            for (GroupMembers groupMembers : groupMembersList) {
                CacheUtil.delete(CacheUtil.GROUP_MEMBERS_CACHE_PREFIX + groupMembers.getGroupId());
            }
        }
    }

    /**
     * 获取融云token
     * 1、根据currentUserId查询User
     * 2、调用融云用户注册接口，获取token
     * 3、根据userId更新本地数据users表中rongCloudToken
     * 4、把userid，token返回给前端
     */
    public Pair<String, String> getToken(Integer currentUserId) throws ServiceException {

        Users user = usersService.getByPrimaryKey(currentUserId);

        //调用融云用户注册接口，获取token
        TokenResult tokenResult = rongCloudClient.register(N3d.encode(user.getId()), user.getNickname(), user.getPortraitUri());
        String token = tokenResult.getToken();

        //根据userId更新本地数据users表中rongCloudToken
        Users param = new Users();
        param.setId(user.getId());
        param.setRongCloudToken(token);
        param.setTimestamp(System.currentTimeMillis());
        param.setUpdatedAt(new Date());
        usersService.updateByPrimaryKeySelective(param);

        return Pair.of(String.valueOf(user.getId()), token);
    }

    /**
     * 获取黑名单列表
     * 1、从cookie中获取currentUserId
     * 2、根据currentUserId从缓存中获取黑名单列表，存在直接返回
     * 3、如果缓存不存在，查询数据库黑名单表，调用融云服务远程获取黑名单列表
     * 4、以融云服务黑名单列表为准，merge更新到数据库
     * 5、缓存到cache、返回最新的黑名单列表
     */
    public String getBlackList(Integer currentUserId) throws ServiceException {
        //从缓存中获取blacklist，存在直接返回
        String blackList = CacheUtil.get(CacheUtil.USER_BLACKLIST_CACHE_PREFIX + currentUserId);
        if (!StringUtils.isEmpty(blackList)) {
            return blackList;
        }
        //查询数据库blacklist表
        Example example = new Example(BlackLists.class);
        example.createCriteria()
                .andEqualTo("userId", currentUserId)
                .andNotEqualTo("friendId", "0")
                .andEqualTo("status", 1);

        List<BlackLists> dbBlackLists = blackListsService.getByExample(example);

        //调用融云服务接口获取黑名单
        BlackListResult blackListResult = rongCloudClient.queryUserBlackList(N3d.encode(currentUserId));

        UserModel[] serverBlackList = blackListResult.getUsers();

        List<Long> serverBlackListIds = new ArrayList<>();

        if (ArrayUtils.isNotEmpty(serverBlackList)) {
            for (UserModel userModel : serverBlackList) {
                long id = N3d.decode(userModel.getId());
                serverBlackListIds.add(id);
            }
        }

        List<Long> dbBlacklistUserIds = new ArrayList<>();

        boolean hasDirtyData = false;
        if (!CollectionUtils.isEmpty(dbBlackLists)) {
            for (BlackLists blackLists : dbBlackLists) {
                if (blackLists.getUsers() != null) {
                    Long userId = Long.valueOf(blackLists.getUsers().getId());
                    dbBlacklistUserIds.add(userId);
                } else {
                    hasDirtyData = true;
                }
            }
        }
        if (hasDirtyData) {
            log.error("Dirty blacklist data currentUserId:{}", currentUserId);
        }

        //比对远程接口获取到的黑名单列表和数据库本地表数据
        //如果远程存在，本地不存在，插入本地数据表
        if (ArrayUtils.isNotEmpty(serverBlackList)) {
            for (UserModel userModel : serverBlackList) {
                int userId = N3d.decode(userModel.getId());
                if (!dbBlacklistUserIds.contains(userModel.getId())) {
                    BlackLists blackLists = new BlackLists();
                    blackLists.setUserId(currentUserId);
                    blackLists.setFriendId(userId);
                    blackLists.setStatus(true);
                    blackLists.setTimestamp(System.currentTimeMillis());
                    blackLists.setCreatedAt(new Date());
                    blackLists.setUpdatedAt(blackLists.getCreatedAt());
                    blackListsService.save(blackLists);
                    log.info("Sync: fix user blacklist, add {} -> {} from db.", currentUserId, userId);
                    //需要每天数据都更新一次数据库吗？TODO
                    long now = System.currentTimeMillis();
                    DataVersions dataVersions = new DataVersions();
                    dataVersions.setUserId(currentUserId);
                    dataVersions.setBlacklistVersion(now);
                    dataVersionsService.updateByPrimaryKeySelective(dataVersions);
                }
            }
        }

        //如果远程不存在，本地存在，逻辑删除本地数据表数据
        if (!CollectionUtils.isEmpty(dbBlacklistUserIds)) {
            for (Long userId : dbBlacklistUserIds) {
                if (!serverBlackListIds.contains(userId)) {
                    BlackLists blackLists = new BlackLists();
                    blackLists.setUpdatedAt(new Date());
                    blackLists.setStatus(false);
                    blackLists.setTimestamp(System.currentTimeMillis());
                    Example example1 = new Example(BlackLists.class);
                    example1.createCriteria().andEqualTo("userId", currentUserId)
                            .andEqualTo("friendId", userId.intValue());

                    blackListsService.updateByExample(blackLists, example1);

                    log.info("Sync: fix user blacklist, remove {} -> {} from db.", currentUserId, userId);
                    //需要每天数据都更新一次数据库吗？TODO
                    long now = System.currentTimeMillis();
                    DataVersions dataVersions = new DataVersions();
                    dataVersions.setUserId(currentUserId);
                    dataVersions.setBlacklistVersion(now);
                    dataVersionsService.updateByPrimaryKeySelective(dataVersions);
                }
            }
        }

        //TODO
        try {
            String results = JacksonUtil.toJson(MiscUtils.encodeResults(dbBlackLists));
            results = MiscUtils.addUpdateTimeToList(results);
            //缓存用户黑名单列表
            CacheUtil.set(CacheUtil.USER_BLACKLIST_CACHE_PREFIX + currentUserId, results);

            return results;

        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
    }


    /**
     * 将好友加入黑名单
     * 1、检查参数，好友ID是否存在用户表中，不存在返回404，friendId is not an available userId.
     * 2、存在则调用融云服务接口新增黑名单
     * 3、将黑名单信息插入或更新本地数据库，然后更新黑名单版本
     * 4、然后清除缓存"user_blacklist_" + currentUserId
     * 5、更新Friendship 表状态信息为 FRIENDSHIP_BLACK = 31
     * 6、然后清除friendship相关缓存
     * -》Cache.del("friendship_profile_displayName_" + currentUserId + "_" + friendId);
     * -》Cache.del("friendship_profile_user_" + currentUserId + "_" + friendId);
     * -》Cache.del("friendship_all_" + currentUserId);
     * -》Cache.del("friendship_all_" + friendId);
     */
    @Transactional(rollbackFor = Exception.class)
    public void addBlackList(Integer currentUserId, Integer friendId, String encodedFriendId) throws ServiceException {

        Users user = usersService.getByPrimaryKey(currentUserId);
        if (user == null) {
            throw new ServiceException(ErrorCode.FRIEND_USER_NOT_EXIST);
        }

        String[] blackFriendIds = {encodedFriendId};
        //调用融云服务接口新增黑名单
        rongCloudClient.addUserBlackList(N3d.encode(currentUserId), blackFriendIds);

        //将黑名单信息插入或更新本地数据库
        blackListsService.saveOrUpdate(currentUserId, friendId, true, System.currentTimeMillis());

        //更新黑名单版本
        long now = System.currentTimeMillis();
        DataVersions dataVersions = new DataVersions();
        dataVersions.setUserId(currentUserId);
        dataVersions.setBlacklistVersion(now);
        dataVersionsService.updateByPrimaryKeySelective(dataVersions);

        //清除user_blacklist_缓存
        CacheUtil.delete(CacheUtil.USER_BLACKLIST_CACHE_PREFIX + currentUserId);

        //更新Friendship 表状态信息为 FRIENDSHIP_BLACK = 31
        Friendships friendships = new Friendships();
        friendships.setDisplayName("");
        friendships.setMessage("");
        friendships.setTimestamp(System.currentTimeMillis());
        friendships.setStatus(Friendships.FRIENDSHIP_PULLEDBLACK);

        Example example = new Example(Friendships.class);
        example.createCriteria().andEqualTo("friendId", friendId)
                .andEqualTo("status", Friendships.FRIENDSHIP_AGREED);
        friendshipsService.updateByExampleSelective(friendships, example);

        //清除friendship相关缓存
        CacheUtil.delete(CacheUtil.FRIENDSHIP_PROFILE_DISPLAYNAME_CACHE_PREFIX + currentUserId + "_" + friendId);
        CacheUtil.delete(CacheUtil.FRIENDSHIP_PROFILE_USER_CACHE_PREFIX + currentUserId + "_" + friendId);
        CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + currentUserId);
        CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + friendId);

        return;
    }

    /**
     * 将好友移除黑名单
     * 1、调用融云服务接口移除黑名单
     * 2、更新本地Blacklist 表，设置记录状态status为false
     * 3、然后更新DataVersion表BlacklistVersion 版本
     * 4、清除缓存user_blacklist_
     * 5、更新Friendship 表状态信息为 FRIENDSHIP_AGREED = 20
     * 4、然后清除相关缓存
     * -》Cache.del("friendship_profile_displayName_" + currentUserId + "_" + friendId);
     * -》Cache.del("friendship_profile_user_" + currentUserId + "_" + friendId);
     * -》Cache.del("friendship_all_" + currentUserId);
     * -》 Cache.del("friendship_all_" + friendId);
     */
    public void removeBlackList(Integer currentUserId, int friendId, String encodedFriendId) throws ServiceException {

        String[] blackFriendIds = {encodedFriendId};
        //调用融云服务接口移除黑名单
        rongCloudClient.removeUserBlackList(N3d.encode(currentUserId), blackFriendIds);

        //更新本地Blacklist 表，设置记录状态status为false
        BlackLists blackLists = new BlackLists();
        blackLists.setStatus(false);
        blackLists.setTimestamp(System.currentTimeMillis());
        blackLists.setUpdatedAt(new Date());

        Example example = new Example(BlackLists.class);
        example.createCriteria().andEqualTo("userId", currentUserId)
                .andEqualTo("friendId", friendId);

        blackListsService.updateByExampleSelective(blackLists, example);

        //更新黑名单版本
        long now = System.currentTimeMillis();
        DataVersions dataVersions = new DataVersions();
        dataVersions.setUserId(currentUserId);
        dataVersions.setBlacklistVersion(now);
        dataVersionsService.updateByPrimaryKeySelective(dataVersions);

        //清除缓存user_blacklist_
        CacheUtil.delete(CacheUtil.USER_BLACKLIST_CACHE_PREFIX + currentUserId);

        //更新Friendship 表状态信息为 FRIENDSHIP_AGREED = 20
        Friendships friendships = new Friendships();
        friendships.setDisplayName("");
        friendships.setMessage("");
        friendships.setTimestamp(System.currentTimeMillis());
        friendships.setStatus(Friendships.FRIENDSHIP_AGREED);

        Example example1 = new Example(Friendships.class);
        example.createCriteria().andEqualTo("friendId", friendId)
                .andEqualTo("status", Friendships.FRIENDSHIP_PULLEDBLACK);
        friendshipsService.updateByExampleSelective(friendships, example);

        log.info("result--remove db black currentUserId={},friendId={}", currentUserId, friendId);
        //清除friendship相关缓存
        CacheUtil.delete(CacheUtil.FRIENDSHIP_PROFILE_DISPLAYNAME_CACHE_PREFIX + currentUserId + "_" + friendId);
        CacheUtil.delete(CacheUtil.FRIENDSHIP_PROFILE_USER_CACHE_PREFIX + currentUserId + "_" + friendId);
        CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + currentUserId);
        CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + friendId);
        log.info("result--remove cache black currentUserId={},friendId={}", currentUserId, friendId);

        return;
    }

    /**
     * 获取用户所属群组
     *  -》先从缓存中获取，缓存中不存在查询db获取
     *
     * @param currentUserId
     * @return
     */
    public String getGroups(Integer currentUserId) throws ServiceException {

        String userGroups = CacheUtil.get(CacheUtil.USER_GROUP_CACHE_PREFIX);
        if (!StringUtils.isEmpty(userGroups)) {
            return userGroups;
        }

        //缓存中为空，去查询db
        List<GroupMembers> groupMembersList = groupMembersService.queryGroupMembersWithGroupByMemberId(currentUserId);

        //TODO
        try {
            userGroups = JacksonUtil.toJson(MiscUtils.encodeResults(groupMembersList));
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }

        // 缓存结果
        if (!CollectionUtils.isEmpty(groupMembersList)) {
            CacheUtil.set(CacheUtil.USER_GROUP_CACHE_PREFIX + currentUserId, userGroups);
        }

        return userGroups;
    }

    /**
     * 根据id查询用户信息
     *
     * @param currentUserId
     * @return
     */
    public Users getUser(Integer currentUserId) {
        return usersService.getByPrimaryKey(currentUserId);
    }

    /**
     * 根据手机号查询用户信息
     * @param region
     * @param phone
     * @return
     */
    public Users getUser(String region,String phone) {
        Users u = new Users();
        u.setRegion(region);
        u.setPhone(phone);
        return usersService.getOne(u);
    }

    /**
     * 设置用户信息
     *
     * @param u
     */
    public void updateUserById(Users u) {
        usersService.updateByPrimaryKeySelective(u);
    }

    /**
     * 设置 SealTlk 号
     * @param currentUserId
     * @param stAccount
     * @throws ServiceException
     */
    public void setStAccount(Integer currentUserId, String stAccount) throws ServiceException {
        Users u = new Users();
        u.setStAccount(stAccount);

        Users users = usersService.getOne(u);

        if (users != null) {
            throw new ServiceException(ErrorCode.EMPTY_STACCOUNT_EXIST);
        }

        u.setId(currentUserId);
        usersService.updateByPrimaryKeySelective(u);

    }

    public String getFavGroups(Integer userId, Integer limit, Integer offset) throws ServiceException {
        List<String> groupsList = new ArrayList<>();
        List<GroupFavs> groupFavsList = groupFavsService.queryGroupFavsWithGroupByUserId(userId, limit, offset);

        if (!CollectionUtils.isEmpty(groupFavsList)) {
            for (GroupFavs groupFavs : groupFavsList) {
                if (groupFavs.getGroups() != null) {
                    //TODO
                    try {
                        groupsList.add(JacksonUtil.toJson(MiscUtils.encodeResults(groupFavs.getGroups())));
                    }catch (Exception e){
                        log.error(e.getMessage(),e);
                        throw new ServiceException(ErrorCode.SERVER_ERROR);
                    }
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        //TODO
        result.put("list", addUpdateTimeToList(groupsList));
        result.put("total", groupsList.size());
        result.put("limit", limit);
        result.put("offset", offset);

        //TODO
        try {
            return JacksonUtil.toJson(result);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
    }
    //TODO
    private Object addUpdateTimeToList(List<String> groupsList) {
        return null;
    }
}

