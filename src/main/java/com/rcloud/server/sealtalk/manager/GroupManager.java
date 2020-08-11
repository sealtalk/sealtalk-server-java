package com.rcloud.server.sealtalk.manager;

import com.rcloud.server.sealtalk.domain.Groups;
import com.rcloud.server.sealtalk.service.GroupsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/11
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
@Slf4j
public class GroupManager extends BaseManager{

    @Resource
    private GroupsService groupsService;


    public List<Groups> getGroupList(List<Integer> groupIds) {

        Example example = new Example(Groups.class);
        example.createCriteria().andIn("id",groupIds);
        return groupsService.getByExample(example);
    }
}
