package com.rcloud.server.sealtalk.rongcloud;

import com.rcloud.server.sealtalk.configuration.SealtalkConfig;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.util.JacksonUtil;
import io.rong.RongCloud;
import io.rong.messages.ContactNtfMessage;
import io.rong.methods.message._private.Private;
import io.rong.methods.message.chatroom.Chatroom;
import io.rong.methods.message.discussion.Discussion;
import io.rong.methods.message.group.Group;
import io.rong.methods.message.history.History;
import io.rong.methods.message.system.MsgSystem;
import io.rong.methods.user.User;
import io.rong.methods.user.blacklist.Blacklist;
import io.rong.models.Result;
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
import java.util.List;
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
    private Group group;
    private Chatroom chatroom;
    private Discussion discussion;
    private History history;

    @PostConstruct
    public void postConstruct() {
        rongCloud = RongCloud.getInstance(sealtalkConfig.getRongcloudAppKey(), sealtalkConfig.getRongcloudAppSecret());
        User = rongCloud.user;
        BlackList = rongCloud.user.blackList;
        Private = rongCloud.message.msgPrivate;
        system = rongCloud.message.system;
        group = rongCloud.message.group;
        chatroom = rongCloud.message.chatroom;
        discussion = rongCloud.message.discussion;
        history = rongCloud.message.history;

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


    public void sendContactNotification(String senderId, String nickname, String[] targetIds,String toUserId, String operation, String messageContent, long timestamp) throws ServiceException {
         RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {

                Map<String,Object> extraInfoMap = new HashMap<>();
                extraInfoMap.put("sourceUserNickname",nickname);
                extraInfoMap.put("version",timestamp);
                String extraInfo = JacksonUtil.toJson(extraInfoMap);
                ContactNtfMessage contactNtfMessage = new ContactNtfMessage(operation,extraInfo,senderId,toUserId,messageContent);

                SystemMessage systemMessage = new SystemMessage()
                        .setSenderId(senderId)
                        .setTargetId(targetIds)
                        .setObjectName(contactNtfMessage.getType())
                        .setContent(contactNtfMessage);
                return rongCloud.message.system.send(systemMessage);
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
    public Result createGroup(String encode, List<String> encodeMemberIds, String name) {


        return null;
    }

    @Override
    public Result joinGroup(String[] memberIds, String groupId, String groupName) {
        return null;
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
