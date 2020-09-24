package com.rcloud.server.sealtalk.dao;

import com.google.common.collect.ImmutableList;
import com.rcloud.server.sealtalk.domain.GroupReceivers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/20
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GroupReceiversMapperTest {
    @Autowired
    private GroupReceiversMapper groupReceiversMapper;


    @Test
    public void name() {
        Integer groupId = 1;
        Integer requesterId = null;
        Integer groupReceiveType=2;
        List<Integer> operatorList = ImmutableList.of(100,101,102);
        List<Integer> receiverIdList = ImmutableList.of(200,201,202);

        List<GroupReceivers> groupReceiversList = groupReceiversMapper.selectReceiversWithList(groupId,requesterId,receiverIdList,operatorList,groupReceiveType);

        System.out.println(groupReceiversList);
    }
}