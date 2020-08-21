package com.rcloud.server.sealtalk.rongcloud;

import com.rcloud.server.sealtalk.configuration.SealtalkConfig;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.rongcloud.message.GrpApplyMessage;
import com.rcloud.server.sealtalk.util.JacksonUtil;
import io.rong.RongCloud;
import io.rong.messages.ContactNtfMessage;
import io.rong.messages.GroupNotificationMessage;
import io.rong.methods.message._private.Private;
import io.rong.methods.message.system.MsgSystem;
import io.rong.methods.user.User;
import io.rong.methods.user.blacklist.Blacklist;
import io.rong.models.Result;
import io.rong.models.group.GroupMember;
import io.rong.models.group.GroupModel;
import io.rong.models.message.GroupMessage;
import io.rong.models.message.PrivateMessage;
import io.rong.models.message.SystemMessage;
import io.rong.models.response.BlackListResult;
import io.rong.models.response.ResponseResult;
import io.rong.models.response.TokenResult;
import io.rong.models.response.UserResult;
import io.rong.models.user.UserModel;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/6
 * @Description: 调用融云服务客户端实现
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class DefaultRongCloudClient implements RongCloudClient {

    @Resource
    private SealtalkConfig sealtalkConfig;

    private RongCloud rongCloud;

    private User User;
    private Blacklist BlackList;

    private Private Private;
    private MsgSystem system;

    @PostConstruct
    public void postConstruct() {
        rongCloud = RongCloud.getInstance(sealtalkConfig.getRongcloudAppKey(), sealtalkConfig.getRongcloudAppSecret());
        User = rongCloud.user;
        BlackList = rongCloud.user.blackList;
        Private = rongCloud.message.msgPrivate;
        system = rongCloud.message.system;
    }


    @Override
    public TokenResult register(String encodeId, String name, String portrait) throws ServiceException {

        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<TokenResult>() {
            @Override
            public TokenResult doInvoker() throws Exception {
                UserModel user = new UserModel()
                        .setId(encodeId)
                        .setName(name)
                        .setPortrait(portrait);

                return User.register(user);
            }
        });
    }

    @Override
    public Result updateUser(String encodeId, String name, String portrait) throws ServiceException {

        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {
                UserModel user = new UserModel()
                        .setId(encodeId)
                        .setName(name)
                        .setPortrait(portrait);

                return User.update(user);
            }
        });

    }

    @Override
    public UserResult getUserInfo(String encodeId) throws ServiceException {
        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<UserResult>() {
            @Override
            public UserResult doInvoker() throws Exception {
                UserModel user = new UserModel()
                        .setId(encodeId);
                return User.get(user);
            }
        });
    }

    @Override
    public Result addUserBlackList(String encodeId, String[] encodeBlackUserIds) throws ServiceException {
        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {

                UserModel[] blacklist = new UserModel[encodeBlackUserIds.length];
                int i = 0;
                for (String blackUserId : encodeBlackUserIds) {
                    UserModel userModel = new UserModel().setId(blackUserId);
                    blacklist[i++] = userModel;
                }
                UserModel user = new UserModel()
                        .setId(encodeId)
                        .setBlacklist(blacklist);

                return BlackList.add(user);
            }
        });
    }

    @Override
    public BlackListResult queryUserBlackList(String encodeId) throws ServiceException {
        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<BlackListResult>() {
            @Override
            public BlackListResult doInvoker() throws Exception {
                UserModel user = new UserModel().setId(encodeId);
                return BlackList.getList(user);
            }
        });
    }


    @Override
    public Result removeUserBlackList(String encodeId, String[] encodeBlackUserIds) throws ServiceException {
        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {

                UserModel[] blacklist = new UserModel[encodeBlackUserIds.length];
                int i = 0;
                for (String blackUserId : encodeBlackUserIds) {
                    UserModel userModel = new UserModel().setId(blackUserId);
                    blacklist[i++] = userModel;
                }
                UserModel user = new UserModel()
                        .setId(encodeId)
                        .setBlacklist(blacklist);

                return BlackList.remove(user);
            }
        });
    }


    public void sendContactNotification(String senderId, String nickname, String[] targetIds, String toUserId, String operation, String messageContent, long timestamp) throws ServiceException {
        RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {

                Map<String, Object> extraInfoMap = new HashMap<>();
                extraInfoMap.put("sourceUserNickname", nickname);
                extraInfoMap.put("version", timestamp);
                String extraInfo = JacksonUtil.toJson(extraInfoMap);
                ContactNtfMessage contactNtfMessage = new ContactNtfMessage(operation, extraInfo, senderId, toUserId, messageContent);

                SystemMessage systemMessage = new SystemMessage()
                        .setSenderId(senderId)
                        .setTargetId(targetIds)
                        .setObjectName(contactNtfMessage.getType())
                        .setContent(contactNtfMessage);
                return system.send(systemMessage);
            }
        });

    }


    @Override
    public ResponseResult sendPrivateMessage(PrivateMessage privateMessage) throws ServiceException {

        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<ResponseResult>() {
            @Override
            public ResponseResult doInvoker() throws Exception {
                return Private.send(privateMessage);
            }
        });
    }

    @Override
    public ResponseResult sendGroupMessage(GroupMessage groupMessage) throws ServiceException {
        //TODO
        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<ResponseResult>() {
            @Override
            public ResponseResult doInvoker() throws Exception {
                return null;
            }
        });
    }

    @Override
    public Result createGroup(String encodeGroupId, String[] encodeMemberIds, String name) throws ServiceException {

        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {
                GroupMember[] members = new GroupMember[encodeMemberIds.length];
                for (int i = 0; i < encodeMemberIds.length; i++) {
                    GroupMember groupMember = new GroupMember();
                    groupMember.setId(encodeMemberIds[i]);
                    members[i] = groupMember;
                }
                GroupModel group = new GroupModel()
                        .setId(encodeGroupId)
                        .setMembers(members)
                        .setName(name);
                Result result = (Result) rongCloud.group.create(group);
                return result;
            }
        });
    }


    @Override
    public Result sendGroupNotificationMessage(String encodeOperatorUserId, String encodeGroupId, String operationType, Map<String, Object> messageData, String message, String extra) throws ServiceException {
        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {

                GroupNotificationMessage groupNotificationMessage = new GroupNotificationMessage(encodeOperatorUserId, operationType, messageData, message, extra);

                GroupMessage groupMessage = new GroupMessage();
                groupMessage.setTargetId(new String[]{encodeGroupId});
                groupMessage.setSenderId(encodeOperatorUserId);
                groupMessage.setObjectName(groupNotificationMessage.getType());
                groupMessage.setContent(groupNotificationMessage);
                return rongCloud.message.group.send(groupMessage);
            }
        });
    }

    @Override
    public Result sendGroupApplyMessage(String senderId, String[] targetId, GrpApplyMessage grpApplyMessage) throws ServiceException {
        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {

                //构建消息内容
                PrivateMessage privateMessage = new PrivateMessage();
                GrpApplyMessage grpApplyMessage = new GrpApplyMessage();

                privateMessage.setSenderId(Constants.GrpApplyMessage_fromUserId);
                privateMessage.setTargetId(targetId);
                privateMessage.setObjectName(grpApplyMessage.getType());
                privateMessage.setContent(grpApplyMessage);
                //发送单聊消息
                return Private.send(privateMessage);
            }
        });
    }

    @Override
    public Result joinGroup(String[] memberIds, String groupId, String groupName) throws ServiceException {
        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {

                GroupMember[] members = new GroupMember[memberIds.length];
                for (int i = 0; i < memberIds.length; i++) {
                    GroupMember groupMember = new GroupMember();
                    groupMember.setId(memberIds[i]);
                    members[i] = groupMember;
                }

                GroupModel group = new GroupModel()
                        .setId(groupId)
                        .setMembers(members)
                        .setName(groupName);
                return rongCloud.group.join(group);
            }
        });
    }

    @Override
    public Result refreshGroupName(String encodedGroupId, String name) {
        return null;
    }

    @Override
    public Result removeGroupWhiteList(String encode, String[] encodedMemberIds) {
        return null;
    }

    @Override
    public Result addGroupWhitelist(String encodedGroupId, String[] encodedMemberIds) {
        return null;
    }

    @Override
    public Result dismiss(String encodeUserId, String encodedGroupId) {
        return null;
    }

    @Override
    public Result quitGroup(String[] encodedMemberIds, String encodedGroupId) {
        return null;
    }

    @Override
    public Result removeMuteStatus(String encodeGroupId) {
        return null;
    }

    @Override
    public Result setMuteStatus(String encode) {
        return null;
    }


}
