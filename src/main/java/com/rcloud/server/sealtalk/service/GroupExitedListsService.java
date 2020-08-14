package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.GroupExitedListsMapper;
import com.rcloud.server.sealtalk.domain.GroupExitedLists;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class GroupExitedListsService extends AbstractBaseService<GroupExitedLists, Integer> {

    @Resource
    private GroupExitedListsMapper mapper;

    @Override
    protected Mapper<GroupExitedLists> getMapper() {
        return mapper;
    }

    /**
     * 删除群组退出列表
     *
     * @param groupId
     * @param quitUserIds
     */
    public void deleteGroupExitedListItems(String groupId, List<Integer> quitUserIds) {

        Example example = new Example(GroupExitedLists.class);
        example.createCriteria().andEqualTo("groupId", groupId)
                .andIn("quitUserId", quitUserIds);
        this.deleteByExample(example);

    }

    //TODO

    /**
     * 新增或修改退群列表
     * <p>
     * //退群有两种情况： 1、自己主动退出，2、 管理员踢
     *
     * @param groupId
     * @param quitUserId
     * @param operatorId
     * @param kickStatus
     */
    public void saveOrUpdate(Integer groupId, Integer quitUserId, Integer operatorId, int kickStatus) {
    }
}
