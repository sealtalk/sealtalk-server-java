package com.rcloud.server.sealtalk.manager;

import com.rcloud.server.sealtalk.configuration.ProfileConfig;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.domain.BlackLists;
import com.rcloud.server.sealtalk.domain.DataVersions;
import com.rcloud.server.sealtalk.domain.Friendships;
import com.rcloud.server.sealtalk.domain.Users;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.model.response.InviteResponse;
import com.rcloud.server.sealtalk.rongcloud.RongCloudClient;
import com.rcloud.server.sealtalk.service.BlackListsService;
import com.rcloud.server.sealtalk.service.DataVersionsService;
import com.rcloud.server.sealtalk.service.FriendshipsService;
import com.rcloud.server.sealtalk.service.UsersService;
import com.rcloud.server.sealtalk.util.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Calendar;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
@Slf4j
public class InviteManager extends BaseManager {

    public static final int VERIFY = 1;

    public static final String NONE = "None";
    public static final String ADDED = "Added";

    @Resource
    private ProfileConfig profileConfig;

    @Resource
    private RongCloudClient rongCloudClient;

    @Resource
    private UsersService usersService;

    @Resource
    private FriendshipsService friendshipsService;

    @Resource
    private BlackListsService blackListsService;

    @Resource
    private DataVersionsService dataVersionsService;

    /**
     * 发起添加好友
     *
     * @param currentUserId
     * @param friendId
     * @param message
     * @return
     * @throws ServiceException
     */
    @Transactional(rollbackFor = Exception.class)
    public InviteResponse invite(Integer currentUserId, Integer friendId, String message)
            throws ServiceException {
        log.info("invite user. currentUserId:[{}] friendId:[{}]", currentUserId, friendId);
        InviteResponse inviteResponse = null;

        Users users = usersService.getByPrimaryKey(friendId);
        Integer friVerify = users.getFriVerify();
        log.info("invite user. friVerify:[{}]", friVerify);
        String action = null;
        if (friVerify == VERIFY) {
            // 需要对方验证
            inviteResponse = addVerifyFriend(currentUserId, friendId, message);
        } else {
            //TODO
            // 不需对方验证直接添加

        }
        return inviteResponse;
    }

    private InviteResponse addVerifyFriend(Integer currentUserId, Integer friendId, String message)
            throws ServiceException {
        String action = NONE;
        String resultMessage = "";
        Friendships f = new Friendships();
        f.setUserId(currentUserId);
        f.setFriendId(friendId);
        Friendships friendshipsCF = friendshipsService.getOne(f);
        f.setUserId(friendId);
        f.setFriendId(currentUserId);
        Friendships friendshipsFC = friendshipsService.getOne(f);

        BlackLists b = new BlackLists();
        b.setUserId(currentUserId);
        b.setFriendId(friendId);
        BlackLists blackLists = blackListsService.getOne(b);

        boolean isInBlackList = checkInBlackList(blackLists, friendshipsCF);
        if (isInBlackList) {
            //在对方黑名单中不能添加好友
            log.info("Invite result. None: blacklisted by friend.");
            resultMessage = "Do nothing.";
            return new InviteResponse(action, resultMessage);
        }
        action = ADDED;
        resultMessage = "Friend added.";
        if (friendshipsCF != null && friendshipsFC != null) {
            //如果双方的好友关系表都有记录，检查记录状态是否已经是好友了
            checkStatus(friendshipsCF.getStatus(), friendshipsFC.getStatus(), friendId);

            long timestamp = System.currentTimeMillis();

            Calendar now_1 = Calendar.getInstance();
            Calendar now_3 = Calendar.getInstance();
            if (Constants.ENV_DEV.equals(profileConfig.getEnv())) {
                now_1.add(Calendar.SECOND, -1);
                now_3.add(Calendar.SECOND, -3);
            } else {
                now_1.add(Calendar.DATE, -1);
                now_3.add(Calendar.DATE, -3);
            }

            Calendar cfUpdateAtCal = Calendar.getInstance();
            cfUpdateAtCal.setTime(friendshipsCF.getUpdatedAt());

            //判断记录的状态
            int cfStatus;
            int fcStatus;

            if (Friendships.FRIENDSHIP_REQUESTING.equals(friendshipsFC.getStatus())) {
                //如果此时对方的好友关系表记录状态 是请求好友中
                //说明二人都有意加对方为好友
                cfStatus = Friendships.FRIENDSHIP_AGREED;
                fcStatus = Friendships.FRIENDSHIP_AGREED;
                message = friendshipsFC.getMessage();

            } else if (Friendships.FRIENDSHIP_AGREED.equals(friendshipsFC.getStatus())) {
                //如果此时对方的好友关系表记录状态 已经同意好友
                cfStatus = Friendships.FRIENDSHIP_AGREED;
                fcStatus = Friendships.FRIENDSHIP_AGREED;
                message = friendshipsFC.getMessage();
                timestamp = friendshipsFC.getTimestamp();

            } else if (judgeCondition(friendshipsCF, friendshipsFC, now_1, now_3, cfUpdateAtCal)) {
                cfStatus = Friendships.FRIENDSHIP_REQUESTING;
                fcStatus = Friendships.FRIENDSHIP_REQUESTED;
                action = "Sent";
                resultMessage = "Request sent.";
            } else {
                action = "None";
                resultMessage = "Do nothing.";
                return new InviteResponse(action, resultMessage);
            }
            //更新好友关系表
            friendshipsCF.setTimestamp(timestamp);
            friendshipsCF.setStatus(cfStatus);
            friendshipsService.updateByPrimaryKey(friendshipsCF);

            friendshipsFC.setTimestamp(timestamp);
            friendshipsFC.setStatus(fcStatus);
            friendshipsFC.setMessage(message);
            friendshipsService.updateByPrimaryKey(friendshipsFC);

            //刷新当前用户dataversion表好友关系数据版本
            refreshFriendshipVersion(currentUserId);

            if (Friendships.FRIENDSHIP_REQUESTED.equals(friendshipsFC.getStatus())) {
                //刷新好友dataversion表好友关系数据版本
                refreshFriendshipVersion(friendId);
                //获取当前用户昵称
                String currentUserNickName = usersService.getCurrentUserNickNameWithCache(currentUserId);
                //调用融云发送通知接口
                rongCloudClient.sendContactNotification(currentUserId, currentUserNickName, friendId, Constants.CONTACT_OPERATION_REQUEST, message, timestamp);
                //清除缓存
                CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + currentUserId);
                CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + friendId);
                //返回Sent 已发送
                return new InviteResponse(action, resultMessage);
            } else {
                //TODO
                //移除黑名单


                //清除缓存

                //返回直接添加
                return new InviteResponse(action, resultMessage);

            }

        } else {
            //TODO
            //如果双方的好友关系表不是都有记录或都没有记录，说明双方还不是好友

            if (currentUserId.equals(friendId)) {
                //如果是添加自己

            } else {
                //如果不是添加自己
            }
        }
        return new InviteResponse(action, resultMessage);
    }

    /**
     * 刷新dataVersions表好友关系版本
     * @param userId
     */
    private void refreshFriendshipVersion(Integer userId) {
        DataVersions dataVersions = new DataVersions();
        dataVersions.setUserVersion(Long.valueOf(userId));
        dataVersions.setFriendshipVersion(System.currentTimeMillis());
        dataVersionsService.updateByPrimaryKeySelective(dataVersions);
    }

    /**
     * 复杂条件判断
     *
     * @param friendshipsCF
     * @param friendshipsFC
     * @param now_1
     * @param now_3
     * @param cfUpdateAtCal
     * @return
     */
    private boolean judgeCondition(Friendships friendshipsCF, Friendships friendshipsFC, Calendar now_1, Calendar now_3, Calendar cfUpdateAtCal) {
        return (
                //条件1 如果双方的好友记录都是已删除
                //可以继续发送请求
                Friendships.FRIENDSHIP_DELETED.equals(friendshipsFC.getStatus()) &&
                        Friendships.FRIENDSHIP_DELETED.equals(friendshipsCF.getStatus())
        ) || (
                //条件2  或者对方的是已忽略，当前用户的是请求中
                //可以继续发送请求
                Friendships.FRIENDSHIP_DELETED.equals(friendshipsFC.getStatus()) &&
                        Friendships.FRIENDSHIP_AGREED.equals(friendshipsCF.getStatus())
        ) || (
                //条件3 并且时间超过了3天（开发环境是1秒）
                Friendships.FRIENDSHIP_REQUESTING.equals(friendshipsCF.getStatus()) &&
                        Friendships.FRIENDSHIP_IGNORED.equals(friendshipsFC.getStatus()) &&
                        now_1.after(cfUpdateAtCal)
        ) || (
                //条件4 并且时间超过了3天（开发环境是1秒）
                Friendships.FRIENDSHIP_REQUESTING.equals(friendshipsCF.getStatus()) &&
                        Friendships.FRIENDSHIP_REQUESTED.equals(friendshipsFC.getStatus()) &&
                        now_3.after(cfUpdateAtCal)
        );
    }

    private void checkStatus(Integer statusCF, Integer statusFC, Integer friendId)
            throws ServiceException {
        if (statusCF == Friendships.FRIENDSHIP_AGREED && statusFC == Friendships.FRIENDSHIP_AGREED) {
            String errorMsg = "User " + friendId + " is already your friend.";
            throw new ServiceException(ErrorCode.REQUEST_ERROR, errorMsg);
        }
    }

    private boolean checkInBlackList(BlackLists blackLists, Friendships friendshipsCF) {
        if (blackLists == null) {
            log.info("blackLists is empty.");
            return false;
        }
        if (!blackLists.getStatus()) {
            log.info("blackLists status is rm.");
            return false;
        }
        if (friendshipsCF.getStatus() == Friendships.FRIENDSHIP_PULLEDBLACK) {
            log.info("blacklisted by friend.");
            return true;
        }
        return false;
    }
}
