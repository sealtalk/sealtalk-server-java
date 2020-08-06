package com.rcloud.server.sealtalk.rongcloud;

import com.rcloud.server.sealtalk.configuration.SealtalkConfig;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.util.N3d;
import io.rong.RongCloud;
import io.rong.methods.user.User;
import io.rong.models.Result;
import io.rong.models.response.TokenResult;
import io.rong.models.response.UserResult;
import io.rong.models.user.UserModel;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

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

    @PostConstruct
    public void postConstruct() {
        rongCloud = RongCloud.getInstance(sealtalkConfig.getRongcloudAppKey(), sealtalkConfig.getRongcloudAppSecret());
        User = rongCloud.user;
    }

    @Override
    public TokenResult register(int id, String name, String portrait) throws ServiceException {

        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<TokenResult>() {
            @Override
            public TokenResult doInvoker() throws Exception {
                UserModel user = new UserModel()
                        .setId(N3d.encode(id)) //n3d编码id
                        .setName(name)
                        .setPortrait(portrait);

                return User.register(user);
            }
        });
    }

    @Override
    public Result updateUser(int id, String name, String portrait) throws ServiceException {

        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<Result>() {
            @Override
            public Result doInvoker() throws Exception {
                UserModel user = new UserModel()
                        .setId(N3d.encode(id)) //n3d编码id
                        .setName(name)
                        .setPortrait(portrait);

                return User.update(user);
            }
        });

    }

    @Override
    public UserResult getUserInfo(int id) throws ServiceException {
        return RongCloudInvokeTemplate.getData(new RongCloudCallBack<UserResult>() {
            @Override
            public UserResult doInvoker() throws Exception {
                UserModel user = new UserModel()
                        .setId(N3d.encode(id)); //n3d编码id

                return User.get(user);
            }
        });
    }
}
