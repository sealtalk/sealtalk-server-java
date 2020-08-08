package com.rcloud.server.sealtalk.manager;

import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.domain.BlackLists;
import com.rcloud.server.sealtalk.domain.Friendships;
import com.rcloud.server.sealtalk.domain.Users;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.model.ServerApiParams;
import com.rcloud.server.sealtalk.model.response.InviteResponse;
import com.rcloud.server.sealtalk.service.BlackListsService;
import com.rcloud.server.sealtalk.service.FriendshipsService;
import com.rcloud.server.sealtalk.service.UsersService;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public static final int FRIENDSHIP_PULLEDBLACK = 31;
    public static final int FRIENDSHIP_AGREED = 20;

    public static final String NONE = "None";
    public static final String ADDED = "Added";

    @Resource
    private UsersService usersService;

    @Resource
    private FriendshipsService friendshipsService;

    @Resource
    private BlackListsService blackListsService;

    public InviteResponse invite(ServerApiParams serverApiParams, Integer friendId, String message)
        throws ServiceException {
        Integer currentUserId = serverApiParams.getServerApiCookie().getCurrentUserId();
        log.info("invite user. currentUserId:[{}] friendId:[{}]", currentUserId, friendId);
        checkMessage(currentUserId, friendId, message);
        Users users = usersService.getByPrimaryKey(friendId);
        Integer friVerify = users.getFriVerify();
        log.info("invite user. friVerify:[{}]", friVerify);
        String action = null;
        if (friVerify == VERIFY) {
            // 需要对方验证
            action = addVerifyFriend(currentUserId, friendId, message);
        } else {
            // 不需对方验证直接添加
        }
        InviteResponse inviteResponse = new InviteResponse();
        inviteResponse.setAction(action);
        return inviteResponse;
    }

    private String addVerifyFriend(Integer currentUserId, Integer friendId, String message)
        throws ServiceException {
        String action = NONE;
        Friendships friendshipsCF = friendshipsService.getInfo(currentUserId, friendId);
        Friendships friendshipsFC = friendshipsService.getInfo(friendId, currentUserId);
        BlackLists blackLists = blackListsService.queryOne(currentUserId, friendId);
        boolean isInBlackList = checkInBlackList(blackLists, friendshipsCF);
        if (isInBlackList) {
            log.info("Invite result. None: blacklisted by friend.");
            return action;
        }
        action = ADDED;
        if (friendshipsCF != null && friendshipsFC != null) {
            checkStatus(friendshipsCF.getStatus(), friendshipsFC.getStatus(), friendId);
        }
        return action;
    }

    private void checkStatus(Integer statusCF, Integer statusFC, Integer friendId)
        throws ServiceException {
        if (statusCF == FRIENDSHIP_AGREED && statusFC == FRIENDSHIP_AGREED) {
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
        if (friendshipsCF.getStatus() == FRIENDSHIP_PULLEDBLACK) {
            log.info("blacklisted by friend.");
            return true;
        }
        return false;
    }

    private void checkMessage(Integer currentUserId, Integer friendId, String message)
        throws ServiceException {
        if (message.length() <= Constants.FRIEND_REQUEST_MESSAGE_MIN_LENGTH
            || message.length() > Constants.FRIEND_REQUEST_MESSAGE_MAX_LENGTH) {
            log.error(
                "Length of friend request message is out of limit. currentUserId:[{}] friendId:[{}] messageLength:[{}]",
                currentUserId, friendId, message.length());
            throw new ServiceException(ErrorCode.REQUEST_ERROR,
                "Length of friend request message is out of limit.");
        }

    }
}
