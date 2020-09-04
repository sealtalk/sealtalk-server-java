package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.domain.GroupReceivers;
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

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/20
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Rollback(true) //设置 true 不会真正插入数据库，为false，数据会插入数据库
public class GroupReceiversServiceTest {

    @Autowired
    private GroupReceiversService groupReceiversService;

    @Test
    public void batchSave() {
        long beginTime = System.currentTimeMillis();

        List<GroupReceivers> groupReceiversList = new ArrayList<>();
        Integer requesterId = 113;
        Integer receiveId = 110;
        Integer groupId = 27;
        String groupName = "测试群for批量插入";
        for(int i=0;i<1003;i++){
            GroupReceivers gr = new GroupReceivers();
            gr.setUserId(receiveId);
            gr.setGroupId(groupId);
            gr.setGroupName(groupName);
            gr.setGroupPortraitUri("http://123.jpg"+i);
            gr.setRequesterId(requesterId);
            gr.setReceiverId(receiveId);
            gr.setStatus(GroupReceivers.GROUP_RECEIVE_STATUS_WAIT);
            gr.setType(GroupReceivers.GROUP_RECEIVE_TYPE_MANAGER);
            gr.setIsRead(0);
            gr.setTimestamp(System.currentTimeMillis());
            gr.setCreatedAt(new Date());
            gr.setUpdatedAt(gr.getCreatedAt());
            groupReceiversList.add(gr);
        }

        groupReceiversService.batchSave(groupReceiversList);

        long endTime = System.currentTimeMillis();

        System.out.println("time cost："+(endTime-beginTime));

    }
}