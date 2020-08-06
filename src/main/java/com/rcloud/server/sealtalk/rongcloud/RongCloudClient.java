package com.rcloud.server.sealtalk.rongcloud;

import com.rcloud.server.sealtalk.exception.ServiceException;
import io.rong.models.Result;
import io.rong.models.response.TokenResult;
import io.rong.models.response.UserResult;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/6
 * @Description: 调用融云服务客户端
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public interface RongCloudClient {

    /**
     * 注册并获取token
     *
     * @param id 用户id  调用方传入int类型id，内部调用融云id需要n3d编码
     * @param name 昵称
     * @param portrait 头像地址
     * @return
     * @throws ServiceException
     */
    TokenResult register(int id, String name, String portrait) throws ServiceException;

    /**
     * 修改用户信息
     *
     * @param id 用户id
     * @param name 昵称
     * @param portrait 头像地址
     * @return
     * @throws ServiceException
     */
    Result updateUser(int id, String name, String portrait) throws ServiceException;

    /**
     * 获取用户信息
     *
     * @param id 用户id
     * @return
     * @throws ServiceException
     */
    UserResult getUserInfo(int id) throws ServiceException;

}
