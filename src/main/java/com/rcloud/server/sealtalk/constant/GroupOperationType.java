package com.rcloud.server.sealtalk.constant;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/12
 * @Description: 群组通知操作类型
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public enum GroupOperationType {
    CREATE("Create"),   //创建群组
    Add("Add"),         //加入群组
    QUIT("Quit"),       //退出群组
    DISMISS("Dismiss"), //解散群组
    KICKED("Kicked"),   //踢人
    RENAME("Rename"),   //重命名群组
    BULLETIN("Bulletin"),   //群公共
    TRANSFER("Transfer"),   //转让群主
    SET_MANAGER("SetManager"),  //设置管理员
    REMOVE_MANAGER("RemoveManager"),    //删除管理员
    INVITE("Invite");       //邀请好友

    private String type;

    GroupOperationType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
