package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.DataVersionsMapper;
import javax.annotation.Resource;

import com.rcloud.server.sealtalk.domain.DataVersions;
import org.springframework.stereotype.Service;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class DataVersionsService {

    @Resource
    private DataVersionsMapper mapper;


    public void createDataVersion(DataVersions dataVersions) {
        mapper.insertSelective(dataVersions);
    }
}
