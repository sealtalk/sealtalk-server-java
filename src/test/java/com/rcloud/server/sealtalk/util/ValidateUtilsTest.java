package com.rcloud.server.sealtalk.util;

import com.rcloud.server.sealtalk.exception.ServiceException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/10/23
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
public class ValidateUtilsTest {


    @Test
    public void checkCompletePhone() throws ServiceException {

        String phone = "19910183283";
        ValidateUtils.checkCompletePhone(phone);
        System.out.println("ok");

    }
}