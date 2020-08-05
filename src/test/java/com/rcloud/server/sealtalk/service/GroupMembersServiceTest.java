package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.domain.GroupMembers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/5
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GroupMembersServiceTest {

    @Autowired
    private GroupMembersService groupMembersService;

    @Test
    public void queryGroupMembersWithGroupByMemberId() {
        int memberId = 1;
        List<GroupMembers> groupMembersList = groupMembersService.queryGroupMembersWithGroupByMemberId(memberId);
        System.out.println(groupMembersList);
    }
}