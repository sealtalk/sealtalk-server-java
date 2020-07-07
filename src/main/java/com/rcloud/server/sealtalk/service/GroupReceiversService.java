package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.GroupReceiversMapper;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class GroupReceiversService {

    @Resource
    private GroupReceiversMapper mapper;
}
