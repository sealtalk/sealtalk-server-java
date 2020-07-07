package com.rcloud.server.sealtalk.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Configuration
@Import({SingleDataSourceConfiguration.class})
@ComponentScans({@ComponentScan("com.rcloud.server.sealtalk.*")})
public class AppConfiguration {

}
