package com.rcloud.server.sealtalk.dao;

import com.rcloud.server.sealtalk.constant.GroupRole;
import com.rcloud.server.sealtalk.domain.GroupMembers;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.util.JacksonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/9/4
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback(true) //设置 true 不会真正插入数据库，为false，数据会插入数据库
public class GroupMembersMapperTest {

    @Autowired
    private GroupMembersMapper groupMembersMapper;

    @Test
    public void insertBatch() throws ServiceException {

        long beginTime = System.currentTimeMillis();

        Integer groupId = 90001;
        Integer memberIdFirst = 10000;
        Integer creatorId = memberIdFirst;


        List<GroupMembers> groupMembersList = new ArrayList<>();
        Integer index = 0;
        int size =1003;
        for(int i=0;i<size;i++){
            GroupMembers groupMembers = new GroupMembers();
            groupMembers.setGroupId(groupId);
            Integer memberId = memberIdFirst +i;
            groupMembers.setMemberId(memberId);
            groupMembers.setRole(memberId.equals(creatorId) ? GroupRole.CREATOR.getCode() : GroupRole.MEMBER.getCode());
            groupMembers.setTimestamp(System.currentTimeMillis());
            groupMembers.setCreatedAt(new Date());
            groupMembers.setUpdatedAt(groupMembers.getCreatedAt());
            groupMembersList.add(groupMembers);
            index++;
            if( index % 1000 == 0 || index.equals(size)){
                int count = groupMembersMapper.insertBatch(groupMembersList);
                System.out.println("count="+count);
                groupMembersList.clear();
            }
        }

        long endTime = System.currentTimeMillis();

        System.out.println("time cost："+(endTime-beginTime));


    }


    @Test
    public void insertBatch2() throws ServiceException {
        long beginTime = System.currentTimeMillis();

        Integer groupId = 90001;
        Integer memberIdFirst = 10000;
        Integer creatorId = memberIdFirst;


        List<GroupMembers> groupMembersList = new ArrayList<>();

        for(int i=0;i<1000;i++){
            GroupMembers groupMembers = new GroupMembers();
            groupMembers.setGroupId(groupId);
            Integer memberId = memberIdFirst +i;
            groupMembers.setMemberId(memberId);
            groupMembers.setRole(memberId.equals(creatorId) ? GroupRole.CREATOR.getCode() : GroupRole.MEMBER.getCode());
            groupMembers.setTimestamp(System.currentTimeMillis());
            groupMembers.setCreatedAt(new Date());
            groupMembers.setUpdatedAt(groupMembers.getCreatedAt());
            groupMembersList.add(groupMembers);
            groupMembersMapper.insertSelective(groupMembers);
        }

        long endTime = System.currentTimeMillis();

        System.out.println("time cost："+(endTime-beginTime));

    }
}