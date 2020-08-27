package com.rcloud.server.sealtalk.controller.param;

import lombok.Data;

/**
 * 添加好友的请求参数
 */
@Data
public class InviteFriendParam {
    /**
     * 好友id
     */
    private String friendId;

    /**
     * 请求信息
     */
    private String message;
}
