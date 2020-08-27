package com.rcloud.server.sealtalk.rongcloud;

import com.rcloud.server.sealtalk.configuration.SealtalkConfig;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.domain.Groups;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.rongcloud.message.CustomerConNtfMessage;
import com.rcloud.server.sealtalk.rongcloud.message.CustomerGroupNtfMessage;
import com.rcloud.server.sealtalk.rongcloud.message.CustomerGroupApplyMessage;
import com.rcloud.server.sealtalk.util.JacksonUtil;
import com.rcloud.server.sealtalk.util.N3d;
import io.rong.RongCloud;
import io.rong.messages.ContactNtfMessage;
import io.rong.messages.GroupNotificationMessage;
import io.rong.messages.TxtMessage;
import io.rong.methods.message._private.Private;
import io.rong.methods.message.system.MsgSystem;
import io.rong.methods.user.User;
import io.rong.methods.user.blacklist.Blacklist;
import io.rong.models.Result;
import io.rong.models.group.GroupMember;
import io.rong.models.group.GroupModel;
import io.rong.models.group.UserGroup;
import io.rong.models.message.*;
import io.rong.models.response.BlackListResult;
import io.rong.models.response.ResponseResult;
import io.rong.models.response.TokenResult;
import io.rong.models.response.UserResult;
import io.rong.models.user.UserModel;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
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


    @PostConstruct
    public void postConstruct() {
        String apiUrlStr = sealtalkConfig.getRongcloudApiUrl();
        if (StringUtils.isEmpty(apiUrlStr)) {
            throw new RuntimeException("rongcloud client init exception");
        }
        String[] apiUrlArray = apiUrlStr.split(",");
        String mainUrl = apiUrlArray[0].trim();
        if (!mainUrl.startsWith("http://") && !mainUrl.startsWith("https://")) {
            mainUrl = "http://" + mainUrl;
        }
        List<String> backUpUrlList = new ArrayList<>();
        if (apiUrlArray.length > 1) {
            for (int i = 1; i < apiUrlArray.length; i++) {
                String backApiUrl = apiUrlArray[i].trim();
                if (!backApiUrl.startsWith("http://") && !backApiUrl.startsWith("https://")) {
                    backApiUrl = "http://" + backApiUrl;
                }
                backUpUrlList.add(backApiUrl);
            }
        }
        rongCloud = RongCloud.getInstance(sealtalkConfig.getRongcloudAppKey(), sealtalkConfig.getRongcloudAppSecret(), mainUrl, backUpUrlList);
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
                groupMessage.setSenderId(encodeOperatorUserId);
                groupMessage.setTargetId(new String[]{encodeGroupId});
                groupMessage.setObjectName(groupNotificationMessage.getType());
                groupMessage.setContent(groupNotificationMessage);
                return rongCloud.message.group.send(groupMessage);
            }
        });
    }

    @Override
    public Result sendCustomerGroupNtfMessage(GroupMessage groupMessage) throws ServiceException {

        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {
                return rongCloud.message.group.send(groupMessage);
            }
        });

    }

    @Override
    public Result sendCustomerGroupNtfMessage(String encodeUserId, String encodeTargetId, String operation) throws ServiceException {

        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {
                CustomerGroupNtfMessage customerGroupNtfMessage = new CustomerGroupNtfMessage();
                customerGroupNtfMessage.setOperatorUserId(encodeUserId);
                customerGroupNtfMessage.setOperation(operation);


                GroupMessage groupMessage = new GroupMessage();
                groupMessage.setTargetId(new String[]{encodeTargetId});
                groupMessage.setSenderId(encodeUserId);
                groupMessage.setObjectName(customerGroupNtfMessage.getType());
                groupMessage.setContent(customerGroupNtfMessage);
                groupMessage.setIsIncludeSender(1);
                return rongCloud.message.group.send(groupMessage);
            }
        });
    }

    @Override
    public Result sendCustomerConNtfMessage(String encodeUserId, String encodeTargetId, String operation) throws ServiceException {
        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {
                CustomerConNtfMessage customerConNtfMessage = new CustomerConNtfMessage();
                customerConNtfMessage.setOperatorUserId(encodeUserId);
                customerConNtfMessage.setOperation(operation);

                GroupMessage groupMessage = new GroupMessage();
                groupMessage.setTargetId(new String[]{encodeTargetId});
                groupMessage.setSenderId(encodeUserId);
                groupMessage.setObjectName(customerConNtfMessage.getType());
                groupMessage.setContent(customerConNtfMessage);
                groupMessage.setIsIncludeSender(1);
                return rongCloud.message.group.send(groupMessage);
            }
        });
    }

    @Override
    public Result sendGroupApplyMessage(String senderId, String[] targetId, CustomerGroupApplyMessage grpApplyMessage) throws ServiceException {
        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {

                //构建消息内容
                PrivateMessage privateMessage = new PrivateMessage();
                CustomerGroupApplyMessage grpApplyMessage = new CustomerGroupApplyMessage();

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
    public Result syncGroupInfo(String encodeUserId, List<Groups> groupsList) throws ServiceException {
        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {

            @Override
            public Result doInvoker() throws Exception {
                GroupModel[] groupModelsArray = new GroupModel[groupsList.size()];

                for (int i = 0; i < groupsList.size(); i++) {
                    GroupModel groupModel = new GroupModel();
                    groupModel.setId(N3d.encode(groupsList.get(i).getId()));
                    groupModel.setName(groupsList.get(i).getName());
                    groupModelsArray[i] = groupModel;
                }

                UserGroup user = new UserGroup()
                        .setId(encodeUserId)
                        .setGroups(groupModelsArray);

                return rongCloud.group.sync(user);
            }
        });
    }

    @Override
    public Result sendBulletinNotification(String fromUserId, String[] toGroupId, String content, Integer type, String[] userIds, String mentionedContent) throws ServiceException {

        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {
                TxtMessage txtMessage = new TxtMessage(content, "");
                //@内容
                MentionedInfo mentionedInfo = new MentionedInfo(type, userIds, mentionedContent);
                //@消息的消息内容
                MentionMessageContent mentionMessageContent = new MentionMessageContent(txtMessage, mentionedInfo);

                MentionMessage mentionMessage = new MentionMessage()
                        .setSenderId(fromUserId)
                        .setTargetId(toGroupId)
                        .setObjectName(txtMessage.getType())
                        .setContent(mentionMessageContent)
                        .setIsIncludeSender(1);
                return rongCloud.message.group.sendMention(mentionMessage);

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
    public Result refreshGroupName(String encodedGroupId, String name) throws ServiceException {
        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {
                GroupModel group = new GroupModel()
                        .setId(encodedGroupId)
                        .setName(name);

                return rongCloud.group.update(group);
            }
        });

    }

    @Override
    public Result removeGroupWhiteList(String encodedGroupId, String[] encodedMemberIds) throws ServiceException {
        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {

                GroupMember[] groupMembers = new GroupMember[encodedMemberIds.length];
                for (int i = 0; i < encodedMemberIds.length; i++) {
                    GroupMember groupMember = new GroupMember();
                    groupMember.setId(encodedMemberIds[i]);
                    groupMembers[i] = groupMember;
                }

                GroupModel groupModel = new GroupModel()
                        .setId(encodedGroupId)
                        .setMembers(groupMembers);
                return rongCloud.group.ban.whitelist.user.remove(groupModel);
            }
        });
    }

    @Override
    public Result addGroupWhitelist(String encodedGroupId, String[] encodedMemberIds) throws ServiceException {
        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {

                GroupMember[] groupMembers = new GroupMember[encodedMemberIds.length];
                for (int i = 0; i < encodedMemberIds.length; i++) {
                    GroupMember groupMember = new GroupMember();
                    groupMember.setId(encodedMemberIds[i]);
                    groupMembers[i] = groupMember;
                }

                GroupModel groupModel = new GroupModel()
                        .setId(encodedGroupId)
                        .setMembers(groupMembers);
                return rongCloud.group.ban.whitelist.user.add(groupModel);
            }
        });
    }

    @Override
    public Result dismiss(String encodeUserId, String encodedGroupId) throws ServiceException {
        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {
                GroupMember[] members = new GroupMember[]{new GroupMember().setId(encodeUserId)};

                GroupModel group = new GroupModel()
                        .setId(encodedGroupId)
                        .setMembers(members);
                return (Result) rongCloud.group.dismiss(group);
            }
        });


    }

    @Override
    public Result quitGroup(String[] encodedMemberIds, String encodedGroupId, String groupName) throws ServiceException {

        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {

                GroupMember[] groupMemberArray = new GroupMember[encodedMemberIds.length];
                for (int i = 0; i < encodedMemberIds.length; i++) {
                    GroupMember groupMember = new GroupMember();
                    groupMember.setId(encodedMemberIds[i]);
                    groupMemberArray[i] = groupMember;
                }
                GroupModel groupModel = new GroupModel()
                        .setId(encodedGroupId)
                        .setMembers(groupMemberArray)
                        .setName(groupName);

                return rongCloud.group.quit(groupModel);
            }
        });
    }

    @Override
    public Result removeMuteStatus(String[] encodeGroupIds) throws ServiceException {

        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {
                return rongCloud.group.ban.remove(encodeGroupIds);
            }
        });

    }

    @Override
    public Result setMuteStatus(String[] encodeGroupIds) throws ServiceException {
        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {
                return rongCloud.group.ban.add(encodeGroupIds);
            }
        });
    }


}
