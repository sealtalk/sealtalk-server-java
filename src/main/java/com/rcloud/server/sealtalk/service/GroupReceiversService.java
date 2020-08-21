package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.GroupReceiversMapper;
import com.rcloud.server.sealtalk.domain.GroupReceivers;
import org.apache.ibatis.annotations.Param;
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
public class GroupReceiversService extends AbstractBaseService<GroupReceivers, Integer> {

    @Resource
    private GroupReceiversMapper mapper;

    @Override
    protected Mapper<GroupReceivers> getMapper() {
        return mapper;
    }

    /**
     * 批量删除GroupReceivers TODO
     *
     * @param groupId
     * @param memberIds
     */
    public void deleteByMemberIds(Integer groupId, Integer[] memberIds) {

        Example example = new Example(GroupReceivers.class);
        example.createCriteria().andEqualTo("groupId", groupId);

        Example.Criteria criteria2 = example.createCriteria();
        for (Integer memberId : memberIds) {
            criteria2.orEqualTo("userId", memberId);
        }
        example.and(criteria2);
    }

    /**
     * 根据groupId、userId删除GroupReverive
     *
     * @param groupId
     * @param userId
     */
    public void deleteGroupReverive(Integer groupId, Integer userId) {

        Example example = new Example(GroupReceivers.class);
        example.createCriteria().andEqualTo("groupId", groupId)
                .andEqualTo("userId", userId);
        this.deleteByExample(example);

    }

    public List<GroupReceivers> getReceiversWithList(Integer groupId,Integer requesterId,List<Integer> receiverIdList, List<Integer> operatorList, Integer groupReceiveType){

        return mapper.selectReceiversWithList(groupId,requesterId,receiverIdList,operatorList,groupReceiveType);
    }


    public int updateReceiversWithList(Integer requesterIdForUpdate,Long timestamp,Integer status,Integer groupId,Integer requesterId,List<Integer> receiverIdList, List<Integer> operatorList, Integer groupReceiveType){

        return mapper.updateReceiversWithList(requesterIdForUpdate,timestamp,status,groupId,requesterId,receiverIdList,operatorList,groupReceiveType);
    }






}
