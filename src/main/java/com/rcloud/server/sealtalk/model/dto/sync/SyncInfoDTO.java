package com.rcloud.server.sealtalk.model.dto.sync;

import com.rcloud.server.sealtalk.domain.BlackLists;
import com.rcloud.server.sealtalk.domain.Friendships;
import com.rcloud.server.sealtalk.domain.GroupMembers;
import com.rcloud.server.sealtalk.domain.Users;
import lombok.Data;

import java.util.List;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/19
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class SyncInfoDTO {

    private Long version;
    private SyncUserDTO user;
    private List<SyncBlackListDTO> blacklist;
    private List<SyncFriendshipDTO> friends;
    private List<SyncGroupDTO> groups;
    private List<SyncGroupMemberDTO> group_members;
}
