package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.domain.Users;
import io.swagger.models.auth.In;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/5
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UsersServiceTest {

    @Autowired
    private UsersService usersService;

    @Test
    public void queryOne() {
        Integer id = 1;
        Users u = usersService.queryOne(id);
        System.out.println(u);
    }

    @Test
    public void testQueryOne() {

        String region="86";
        String phone="18810183283";

        Users u = usersService.queryOne(region,phone);
        System.out.println(u.toString());
    }

    @Test
    public void createUser() {
        Users u = new Users();
        u.setNickname("zhangsan");
        u.setRegion("86");
        u.setPhone("18810183283");
        u.setPasswordHash("password111");
        u.setPasswordSalt("1234");
        u.setCreatedAt(new Date());
        u.setUpdatedAt(u.getCreatedAt());
        long id = usersService.createUser(u);
        System.out.println(id);
    }
}