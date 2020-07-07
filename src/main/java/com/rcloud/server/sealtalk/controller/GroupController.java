package com.rcloud.server.sealtalk.controller;

import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Api(tags = "群组相关")
@RestController
@RequestMapping("/group")
@Timed(percentiles = {0.9, 0.95, 0.99})
@Slf4j
public class GroupController {

}
