package com.rcloud.server.sealtalk.rongcloud;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public interface RongCloudCallBack<T> {

    T doInvoker() throws Exception;
}
