package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.domain.Users;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/7
 * @Description: BaseService 方法测试类
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UsersServiceTest2 {

    @Resource
    private UsersService usersService;

    @Test
    public void testGetByPrimaryKey() {
        Integer id = 1;
        Users users = usersService.getByPrimaryKey(id);
        System.out.println(users);
    }

    @Test
    public void testSaveSelective() {
        Users u = new Users();
        u.setNickname("nickname-zhangsan");
        u.setRegion("86");
        u.setPhone("18922222225");
        u.setPasswordHash("passwordHashStr");
        u.setPasswordSalt("1234");
        u.setCreatedAt(new Date());
        u.setUpdatedAt(u.getCreatedAt());
        int id = usersService.saveSelective(u);
        System.out.println(id);
        System.out.println(u);
        System.out.println("u.id:"+u.getId());
    }

    @Test
    public void testSave() {

        Users u = new Users();
        u.setNickname("nickname-zhangsan");
        u.setRegion("86");
        u.setPhone("18922222229");
        u.setPasswordHash("passwordHashStr");
        u.setPasswordSalt("1234");
        u.setPortraitUri("http://xxxtest.com");
        u.setRongCloudToken("test-token");
        u.setStAccount("st_account_test");
        u.setGender("male");
        u.setPhoneVerify(1);
        u.setFriVerify(1);
        u.setStSearchVerify(1);
        u.setGroupVerify(1);
        u.setPokeStatus(1);
        u.setGroupCount(1);
        u.setTimestamp(System.currentTimeMillis());
        u.setCreatedAt(new Date());
        u.setUpdatedAt(u.getCreatedAt());
        u.setDeletedAt(new Date());

        int id = usersService.save(u);
        System.out.println(id);
        System.out.println(u);
        System.out.println("u.id:"+u.getId());

    }


    @Test
    public void testUpdateByPrimaryKeySelective() {
        Users u = new Users();
        u.setNickname("nickname-zhangsan111");
        u.setRegion("87");
        u.setPhone("18922222229");
        u.setPasswordHash("passwordHashStr");
        u.setPasswordSalt("9999");
        u.setGroupCount(0);
        u.setId(9);

        int count = usersService.updateByPrimaryKeySelective(u);
        System.out.println(count);
    }

    @Test
    public void testUpdateByPrimaryKey() {
        Users u = new Users();
        u.setNickname("nickname-zhangsan");
        u.setRegion("86");
        u.setPhone("18922222229");
        u.setPasswordHash("passwordHashStr");
        u.setPasswordSalt("6666");
        u.setPortraitUri("http://xxxtest.com");
        u.setRongCloudToken("test-token");
        u.setStAccount("st_account_test");
        u.setGender("male");
        u.setPhoneVerify(0);
        u.setFriVerify(1);
        u.setStSearchVerify(0);
        u.setGroupVerify(1);
        u.setPokeStatus(1);
        u.setGroupCount(1);
        u.setTimestamp(System.currentTimeMillis());
        u.setCreatedAt(new Date());
        u.setUpdatedAt(u.getCreatedAt());
        u.setDeletedAt(new Date());
        u.setId(9);

        int count = usersService.updateByPrimaryKey(u);
        System.out.println(count);
    }


}