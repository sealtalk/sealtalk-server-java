package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.domain.DataVersions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/5
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DataVersionsServiceTest {

    @Autowired
    private DataVersionsService dataVersionsService;

    @Test
    public void createDataVersion() {

        DataVersions dataVersions = new DataVersions();
        dataVersions.setUserId(1);
        dataVersionsService.saveSelective(dataVersions);
    }
}