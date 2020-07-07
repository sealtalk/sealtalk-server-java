package com.rcloud.server.sealtalk.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/7
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
@Component
public class ProfileConfig {

    @Value("${spring.profiles.active}")
    private String env;
}
