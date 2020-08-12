package com.rcloud.server.sealtalk.manager;

import com.google.common.collect.ImmutableList;
import com.rcloud.server.sealtalk.configuration.ProfileConfig;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.domain.BlackLists;
import com.rcloud.server.sealtalk.domain.DataVersions;
import com.rcloud.server.sealtalk.domain.Friendships;
import com.rcloud.server.sealtalk.domain.Users;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.model.response.dto.ContractInfoDTO;
import com.rcloud.server.sealtalk.model.response.dto.FriendDTO;
import com.rcloud.server.sealtalk.model.response.dto.InviteDTO;
import com.rcloud.server.sealtalk.rongcloud.RongCloudClient;
import com.rcloud.server.sealtalk.service.BlackListsService;
import com.rcloud.server.sealtalk.service.DataVersionsService;
import com.rcloud.server.sealtalk.service.FriendshipsService;
import com.rcloud.server.sealtalk.service.UsersService;
import com.rcloud.server.sealtalk.util.CacheUtil;
import com.rcloud.server.sealtalk.util.JacksonUtil;
import com.rcloud.server.sealtalk.util.N3d;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * @Author: xiuwei.nie
 * @Author: Jianlu.Yu
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
@Slf4j
public class FriendShipManager extends BaseManager {

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
    public InviteDTO invite(Integer currentUserId, Integer friendId, String message)
            throws ServiceException {
        log.info("invite user. currentUserId:[{}] friendId:[{}]", currentUserId, friendId);
        InviteDTO inviteResponse = null;

        Users users = usersService.getByPrimaryKey(friendId);
        Integer friVerify = users.getFriVerify();
        log.info("invite user. friVerify:[{}]", friVerify);
        String action = null;
        if (friVerify == VERIFY) {
            // 需要对方验证
            inviteResponse = addVerifyFriend(currentUserId, friendId, message);
        } else {
            // 不需对方验证直接添加
            inviteResponse = addNoNeedVerifyFriend(currentUserId, friendId, message);
        }
        return inviteResponse;
    }

    private InviteDTO addVerifyFriend(Integer currentUserId, Integer friendId, String message)
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
            return new InviteDTO(action, resultMessage);
        }
        action = ADDED;
        resultMessage = "Friend added.";
        long timestamp = System.currentTimeMillis();

        if (friendshipsCF != null && friendshipsFC != null) {
            //如果双方的好友关系表都有记录，检查记录状态是否已经是好友了
            checkStatus(friendshipsCF.getStatus(), friendshipsFC.getStatus(), friendId);
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
                return new InviteDTO(action, resultMessage);
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
            refreshFriendshipVersion(currentUserId, timestamp);

            if (Friendships.FRIENDSHIP_REQUESTED.equals(friendshipsFC.getStatus())) {
                //刷新好友dataversion表好友关系数据版本
                refreshFriendshipVersion(friendId, timestamp);
                //获取当前用户昵称
                String currentUserNickName = usersService.getCurrentUserNickNameWithCache(currentUserId);
                //调用融云发送通知接口
                rongCloudClient.sendContactNotification(currentUserId, currentUserNickName, friendId, Constants.CONTACT_OPERATION_REQUEST, message, timestamp);
                //清除缓存
                CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + currentUserId);
                CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + friendId);
                //返回Sent 已发送
                return new InviteDTO(action, resultMessage);
            } else {
                //移除黑名单
                removeBlackList(currentUserId, friendId);
                //清除缓存
                CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + currentUserId);
                CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + currentUserId);
                //返回
                return new InviteDTO(action, resultMessage);

            }

        } else {
            //TODO
            //如果双方的好友关系表不是都有记录或都没有记录，说明双方还不是好友

            if (currentUserId.equals(friendId)) {
                //如果是添加自己,新增一条好友关系记录
                Friendships friendship = new Friendships();
                friendship.setUserId(currentUserId);
                friendship.setFriendId(friendId);
                friendship.setStatus(Friendships.FRIENDSHIP_AGREED);
                friendship.setTimestamp(timestamp);
                friendshipsService.saveSelective(friendship);
                refreshFriendshipVersion(currentUserId, timestamp);
                //清除缓存
                CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + currentUserId);
                CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + currentUserId);
                //返回
                return new InviteDTO(action, resultMessage);

            } else {
                //如果不是添加自己
                //添加当前用户好友关系，状态FRIENDSHIP_REQUESTING 请求中
                Friendships friendship = new Friendships();
                friendship.setUserId(currentUserId);
                friendship.setFriendId(friendId);
                friendship.setStatus(Friendships.FRIENDSHIP_REQUESTING);
                friendship.setMessage("");
                friendship.setTimestamp(timestamp);
                friendshipsService.saveSelective(friendship);

                //添加对方好友关系，状态FRIENDSHIP_REQUESTED 被请求
                Friendships friendship2 = new Friendships();
                friendship2.setUserId(currentUserId);
                friendship2.setFriendId(friendId);
                friendship2.setStatus(Friendships.FRIENDSHIP_REQUESTED);
                friendship2.setMessage(message);
                friendship2.setTimestamp(timestamp);
                friendshipsService.saveSelective(friendship);
                //刷新好友关系数据版本
                refreshFriendshipVersion(currentUserId, timestamp);
                refreshFriendshipVersion(friendId, timestamp);

                //获取当前用户昵称
                String currentUserNickName = usersService.getCurrentUserNickNameWithCache(currentUserId);
                //调用融云发送通知接口
                rongCloudClient.sendContactNotification(currentUserId, currentUserNickName, friendId, Constants.CONTACT_OPERATION_REQUEST, message, timestamp);
                //清除缓存
                CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + currentUserId);
                CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + friendId);
                //返回Sent 已发送
                action = "Sent";
                resultMessage = "Request sent.";
                return new InviteDTO(action, resultMessage);
            }
        }
    }

    private InviteDTO addNoNeedVerifyFriend(Integer currentUserId, Integer friendId, String message) throws ServiceException {
        String action = NONE;
        String resultMessage = "";
        long timestamp = System.currentTimeMillis();

        //移除黑名单
        removeBlackList(currentUserId, friendId);

        //插入或更新当前用户好友关系表状态为FRIENDSHIP_AGREED
        Friendships friendship = new Friendships();
        friendship.setUserId(currentUserId);
        friendship.setFriendId(friendId);
        friendship.setMessage(message);
        friendship.setStatus(Friendships.FRIENDSHIP_AGREED);
        friendship.setTimestamp(timestamp);
        //TODO
        friendshipsService.saveOrUpdate(friendship, currentUserId, friendId);

        //插入或更新对方用户好友关系表状态为FRIENDSHIP_AGREED
        Friendships friendship2 = new Friendships();
        friendship2.setUserId(friendId);
        friendship2.setFriendId(currentUserId);
        friendship2.setMessage(message);
        friendship2.setStatus(Friendships.FRIENDSHIP_AGREED);
        friendship2.setTimestamp(timestamp);
        friendshipsService.saveOrUpdate(friendship2, friendId, currentUserId);

        //获取当前用户昵称
        String currentUserNickName = usersService.getCurrentUserNickNameWithCache(currentUserId);
        //调用融云发送通知接口
        rongCloudClient.sendContactNotification(currentUserId, currentUserNickName, friendId, Constants.CONTACT_OPERATION_REQUEST, message, timestamp);
        //清除缓存
        CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + currentUserId);
        CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + friendId);
        //返回Sent 已发送
        action = "AddDirectly";
        resultMessage = "Request sent.";
        return new InviteDTO(action, resultMessage);

    }

    private void updateBlackListStatus(Integer currentUserId, Integer friendId) {
        BlackLists bl = new BlackLists();
        bl.setFriendId(friendId);
        bl.setUserId(currentUserId);
        bl.setStatus(false);
        Example example = new Example(BlackLists.class);
        example.createCriteria().andEqualTo("userId", currentUserId)
                .andEqualTo("friendId", friendId);
        blackListsService.updateByExample(bl, example);
    }

    /**
     * 刷新dataVersions表好友关系版本
     *
     * @param userId
     */
    private void refreshFriendshipVersion(Integer userId, long timestamp) {
        DataVersions dataVersions = new DataVersions();
        dataVersions.setUserVersion(Long.valueOf(userId));
        dataVersions.setFriendshipVersion(timestamp);
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

    /**
     * 同意添加好友
     *
     * @param currentUserId
     * @param friendId
     * @throws ServiceException
     */
    @Transactional(rollbackFor = Exception.class)
    public void agree(Integer currentUserId, Integer friendId) throws ServiceException {
        long timestamp = System.currentTimeMillis();
        //删除黑名单
        removeBlackList(currentUserId, friendId);
        //更新当前用户好友关系状态
        int effectedCount = friendshipsService.updateAgreeStatus(currentUserId, friendId, timestamp, ImmutableList.of(Friendships.FRIENDSHIP_REQUESTED, Friendships.FRIENDSHIP_AGREED));
        if (effectedCount == 0) {
            throw new ServiceException(ErrorCode.UNKNOW_FRIEND_USER_OR_INVALID_STATUS, "Unknown friend user or invalid status.");
        }
        //更新对方Friendship好友关系表中的状态为FRIENDSHIP_AGREED
        Friendships friendships = new Friendships();
        friendships.setStatus(Friendships.FRIENDSHIP_AGREED);
        friendships.setTimestamp(timestamp);
        Example example = new Example(Friendships.class);
        friendshipsService.updateByExample(friendships, example);

        //刷新好友关系数据版本
        refreshFriendshipVersion(currentUserId, timestamp);
        refreshFriendshipVersion(friendId, timestamp);

        //获取当前用户昵称
        String currentUserNickName = usersService.getCurrentUserNickNameWithCache(currentUserId);
        //调用融云发送通知接口
        rongCloudClient.sendContactNotification(currentUserId, currentUserNickName, friendId, Constants.CONTACT_OPERATION_REQUEST, "", timestamp);
        //清除缓存
        CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + currentUserId);
        CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + friendId);
        return;
    }

    /**
     * 删除黑名单
     * 1）调用远程接口删除黑名单
     * 2）更新本地黑名单表数据状态
     * 3）清除USER_BLACKLIST_CACHE_PREFIX 缓存
     *
     * @param currentUserId
     * @param friendId
     * @throws ServiceException
     */
    private void removeBlackList(Integer currentUserId, Integer friendId) throws ServiceException {
        rongCloudClient.removeBlackList(currentUserId, new String[]{N3d.encode(friendId)});
        rongCloudClient.removeBlackList(friendId, new String[]{N3d.encode(currentUserId)});
        updateBlackListStatus(currentUserId, friendId);
        updateBlackListStatus(friendId, currentUserId);
        CacheUtil.delete(CacheUtil.USER_BLACKLIST_CACHE_PREFIX + currentUserId);
    }

    /**
     * 忽略好友请求
     *
     * @param currentUserId
     * @param friendId
     * @throws ServiceException
     */
    @Transactional(rollbackFor = Exception.class)
    public void ignore(Integer currentUserId, int friendId) throws ServiceException {

        long timestamp = System.currentTimeMillis();
        //更新对方Friendship好友关系表中的状态为FRIENDSHIP_AGREED
        Friendships friendships = new Friendships();
        friendships.setStatus(Friendships.FRIENDSHIP_IGNORED);
        friendships.setTimestamp(timestamp);
        Example example = new Example(Friendships.class);
        example.createCriteria().andEqualTo("status", Friendships.FRIENDSHIP_REQUESTED)
                .andEqualTo("userId", currentUserId)
                .andEqualTo("friendId", friendId);

        int effectedCount = friendshipsService.updateByExample(friendships, example);
        if (effectedCount == 0) {
            throw new ServiceException(ErrorCode.UNKNOW_FRIEND_USER_OR_INVALID_STATUS, "Unknown friend user or invalid status.");
        }
        refreshFriendshipVersion(currentUserId, timestamp);
        CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + currentUserId);
        CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + friendId);

        return;
    }

    /**
     * 删除好友
     *
     * @param currentUserId
     * @param friendId
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer currentUserId, int friendId) throws ServiceException {

        long timestamp = System.currentTimeMillis();

        Friendships friendships = new Friendships();
        friendships.setStatus(Friendships.FRIENDSHIP_DELETED);
        friendships.setDisplayName("");
        friendships.setMessage("");
        friendships.setTimestamp(timestamp);

        Example example = new Example(Friendships.class);
        example.createCriteria().andEqualTo("userId", currentUserId);
        example.createCriteria().andEqualTo("friendId", friendId);
        example.createCriteria().andIn("status", ImmutableList.of(Friendships.FRIENDSHIP_AGREED, Friendships.FRIENDSHIP_PULLEDBLACK));
        int effectedCount = friendshipsService.updateByExample(friendships, example);

        if (effectedCount == 0) {
            throw new ServiceException(ErrorCode.UNKNOW_FRIEND_USER_OR_INVALID_STATUS, "Unknown friend user or invalid status.");
        }

        refreshFriendshipVersion(currentUserId, timestamp);

        Users u = usersService.getByPrimaryKey(friendId);

        if (u != null) {
            //调用融云黑名单接口新增,删除好友（请求），相当于告诉融云服务端把删除的好友加入黑名单不在接收他发的消息
            rongCloudClient.addBlackList(currentUserId, new String[]{N3d.encode(friendId)});
            //同时插入或更新本地黑名单表
            blackListsService.saveOrUpdate(currentUserId, friendId, true, timestamp);
            //刷新黑名单版本
            DataVersions dataVersions = new DataVersions();
            dataVersions.setUserVersion(new Long(currentUserId));
            dataVersionsService.updateByPrimaryKeySelective(dataVersions);

            //清除黑名单缓存
            CacheUtil.delete(CacheUtil.USER_BLACKLIST_CACHE_PREFIX + currentUserId);
            //更新好友关系表状态FRIENDSHIP_DELETED
            Friendships friendships2 = new Friendships();
            friendships2.setStatus(Friendships.FRIENDSHIP_DELETED);
            friendships2.setDisplayName("");
            friendships2.setMessage("");
            friendships2.setTimestamp(timestamp);

            Example example2 = new Example(Friendships.class);
            example2.createCriteria().andEqualTo("userId", currentUserId);
            example2.createCriteria().andEqualTo("friendId", friendId);
            example2.createCriteria().andIn("status", ImmutableList.of(Friendships.FRIENDSHIP_AGREED));
            friendshipsService.updateByExample(friendships, example);

            //清除相关缓存
            CacheUtil.delete(CacheUtil.FRIENDSHIP_PROFILE_USER_CACHE_PREFIX + currentUserId + "_" + friendId);
            CacheUtil.delete(CacheUtil.FRIENDSHIP_PROFILE_DISPLAYNAME_CACHE_PREFIX + currentUserId + "_" + friendId);
            CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + currentUserId);
            CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + friendId);

            return;
        } else {
            throw new ServiceException(ErrorCode.UNKNOW_FRIEND_USER_OR_INVALID_STATUS);
        }

    }

    /**
     * 设置好友备注名
     *
     * @param currentUserId
     * @param friendId
     * @param displayName
     */
    public void setDisplayName(Integer currentUserId, int friendId, String displayName) throws ServiceException {
        long timestamp = System.currentTimeMillis();

        Friendships friendships = new Friendships();
        friendships.setStatus(Friendships.FRIENDSHIP_DELETED);
        friendships.setDisplayName(displayName);
        friendships.setTimestamp(timestamp);

        Example example = new Example(Friendships.class);
        example.createCriteria().andEqualTo("userId", currentUserId);
        example.createCriteria().andEqualTo("friendId", friendId);
        example.createCriteria().andEqualTo("status", Friendships.FRIENDSHIP_AGREED);
        int effectedCount = friendshipsService.updateByExample(friendships, example);
        if (effectedCount == 0) {
            throw new ServiceException(ErrorCode.UNKNOW_FRIEND_USER_OR_INVALID_STATUS, "Unknown friend user or invalid status.");
        }

        refreshFriendshipVersion(currentUserId, timestamp);

        CacheUtil.delete(CacheUtil.FRIENDSHIP_PROFILE_DISPLAYNAME_CACHE_PREFIX + currentUserId + "_" + friendId);
        CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + currentUserId);
        return;

    }

    /**
     * 获取当前用户好友列表
     *
     * @param currentUserId
     * @return
     */
    public String getFriendList(Integer currentUserId) throws ServiceException {

        String result = CacheUtil.get(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + currentUserId);

        if (result != null) {
            return result;
        } else {
            List<Friendships> friendships = friendshipsService.getFriendShipListWithUsers(currentUserId);
            if (!CollectionUtils.isEmpty(friendships)) {
                result = JacksonUtil.toJson(friendships);
                CacheUtil.set(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + currentUserId, result);
            }
        }

        return result;
    }

    /**
     * 获取好友信息
     *
     * @param currentUserId
     * @param friendId
     * @return
     */
    public String getFriendProfile(Integer currentUserId, int friendId) throws ServiceException {


        String result = CacheUtil.get(CacheUtil.FRIENDSHIP_PROFILE_DISPLAYNAME_CACHE_PREFIX + "_" + currentUserId + "_" + friendId);
        Friendships friendships = friendshipsService.getFriendShipWithUsers(currentUserId, friendId, Friendships.FRIENDSHIP_AGREED);

        if (friendships == null) {
            throw new ServiceException(ErrorCode.NOT_FRIEND_USER, "Current user is not friend of user " + currentUserId + ".");
        } else {
            result = JacksonUtil.toJson(result);
            CacheUtil.set(CacheUtil.FRIENDSHIP_PROFILE_DISPLAYNAME_CACHE_PREFIX + "_" + currentUserId + "_" + friendId, result);

            CacheUtil.set(CacheUtil.FRIENDSHIP_PROFILE_DISPLAYNAME_CACHE_PREFIX + currentUserId + "_" + friendId, friendships.getDisplayName());
            CacheUtil.set(CacheUtil.FRIENDSHIP_PROFILE_USER_CACHE_PREFIX + currentUserId + "_" + friendId, JacksonUtil.toJson(friendships.getUsers()));

            return result;

        }
    }

    /**
     * 获取手机通讯录好友列表
     *
     * @param currentUserId
     * @param contacstList
     */
    public List<ContractInfoDTO> getContactsInfo(Integer currentUserId, String[] contacstList) throws ServiceException {

        List<ContractInfoDTO> contractInfoDTOList = new ArrayList<>();

        Example example = new Example(Users.class);
        example.createCriteria().andIn("phone", CollectionUtils.arrayToList(contacstList));
        List<Users> usersList = usersService.getByExample(example);
        List<Integer> registerUserIdList = new ArrayList<>();
        Map<String, Users> registerUsers = new HashMap<>();

        if (!CollectionUtils.isEmpty(usersList)) {
            for (Users users : usersList) {
                registerUserIdList.add(users.getId());
                registerUsers.put(users.getPhone(), users);
            }
        }

        Example friendShipExample = new Example(Friendships.class);
        friendShipExample.createCriteria().andEqualTo("userId", currentUserId)
                .andEqualTo("status", Friendships.FRIENDSHIP_AGREED)
                .andIn("friendId", CollectionUtils.arrayToList(contacstList));

        List<Friendships> friendshipsList = friendshipsService.getByExample(example);

        List<Integer> friendIdList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(friendshipsList)) {
            for (Friendships friendships : friendshipsList) {
                friendIdList.add(friendships.getId());
            }
        }

        for (String phone : contacstList) {
            ContractInfoDTO contractInfoDTO = new ContractInfoDTO();
            Users users = registerUsers.get(phone);
            if (users == null) {
                contractInfoDTO.setRegistered(ContractInfoDTO.UN_REGISTERED);
                contractInfoDTO.setRelationship(ContractInfoDTO.NON_FRIEND);
                contractInfoDTO.setStAccount("");
                contractInfoDTO.setPhone("");
                contractInfoDTO.setId("");
                contractInfoDTO.setNickname("");
                contractInfoDTO.setPortraitUri("");

            } else {
                contractInfoDTO.setRegistered(ContractInfoDTO.REGISTERED);
                if (friendIdList.contains(users.getId())) {
                    contractInfoDTO.setRelationship(ContractInfoDTO.IS_FRIEND);
                } else {
                    contractInfoDTO.setRelationship(ContractInfoDTO.NON_FRIEND);
                }
                contractInfoDTO.setId(N3d.encode(users.getId()));
                contractInfoDTO.setNickname(users.getNickname());
                contractInfoDTO.setPortraitUri(users.getPortraitUri());
                contractInfoDTO.setStAccount(users.getStAccount());
                contractInfoDTO.setPhone(phone);
            }
            contractInfoDTOList.add(contractInfoDTO);
        }
        return contractInfoDTOList;
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(Integer currentUserId, String[] friendIds) throws ServiceException {

        long timestamp = System.currentTimeMillis();
        Friendships friendships = new Friendships();
        friendships.setStatus(Friendships.FRIENDSHIP_DELETED);
        friendships.setDisplayName("");
        friendships.setMessage("");
        friendships.setTimestamp(timestamp);

        Example example = new Example(Friendships.class);
        example.createCriteria().andEqualTo("status", Friendships.FRIENDSHIP_AGREED)
                .andEqualTo("userId", currentUserId)
                .andIn("friendId", CollectionUtils.arrayToList(friendIds));

        friendshipsService.updateByExample(friendships, example);

        //更新成功后添加到 IM 黑名单
        String[] encodeFriendIds = new String[friendIds.length];
        int i = 0;
        for (String fId : friendIds) {
            encodeFriendIds[i++] = N3d.encode(Integer.valueOf(fId));
        }

        rongCloudClient.addBlackList(currentUserId, encodeFriendIds);
    }

    /**
     * 设置朋友备注和描述
     *
     * @param friendId
     * @param displayName
     * @param region
     * @param phone
     * @param description
     * @param imageUri
     */
    public void setFriendDescription(Integer currentUserId, String friendId, String displayName, String region, String phone, String description, String imageUri) throws ServiceException {

        Example example = new Example(Friendships.class);
        example.createCriteria().andEqualTo("userId", currentUserId)
                .andEqualTo("friendId", friendId);

        Friendships friendships = friendshipsService.getOneByExample(example);

        if (friendships != null) {

            Friendships newFriendships = new Friendships();
            if (displayName != null) {
                newFriendships.setDisplayName(displayName);
            }

            if (region != null && phone != null) {
                newFriendships.setRegion(region);
                newFriendships.setPhone(phone);
            }

            if (description != null) {
                newFriendships.setDescription(description);
            }

            if (imageUri != null) {
                newFriendships.setImageUri(imageUri);
            }

            friendshipsService.updateByExample(newFriendships, example);
            CacheUtil.delete(CacheUtil.FRIENDSHIP_ALL_CACHE_PREFIX + currentUserId);

            return;
        } else {
            throw new ServiceException(ErrorCode.ILLEGAL_PARAMETER);
        }

    }

    /**
     * 获取朋友备注和名称
     *
     * @param currentUserId
     * @param friendId
     */
    public FriendDTO getFriendDescription(Integer currentUserId, String friendId) {
        FriendDTO friendDTO = new FriendDTO();
        Example example = new Example(Friendships.class);
        example.createCriteria().andEqualTo("userId", currentUserId)
                .andEqualTo("friendId", friendId);
        example.selectProperties("displayName", "region", "phone", "description", "imageUri");

        Friendships friendships = friendshipsService.getOneByExample(example);

        if (friendships != null) {
            friendDTO.setDescription(friendships.getDescription());
            friendDTO.setDisplayName(friendships.getDisplayName());
            friendDTO.setImageUri(friendships.getImageUri());
            friendDTO.setRegion(friendships.getRegion());
            friendDTO.setPhone(friendships.getPhone());
        }
        return friendDTO;
    }
}
