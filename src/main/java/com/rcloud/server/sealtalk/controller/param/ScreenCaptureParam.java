package com.rcloud.server.sealtalk.controller.param;

import lombok.Data;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/24
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class ScreenCaptureParam {

    private Integer conversationType; //会话类型：1 单聊、3 群聊
    private String targetId;        //接收者 Id
    private Integer noticeStatus;   //设置状态： 0 关闭 1 打开


}
