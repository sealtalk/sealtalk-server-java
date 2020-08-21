package com.rcloud.server.sealtalk.service;

import com.rcloud.server.sealtalk.dao.GroupSyncsMapper;
import com.rcloud.server.sealtalk.domain.GroupSyncs;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.common.Mapper;

import javax.annotation.Resource;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Service
public class GroupSyncsService extends AbstractBaseService<GroupSyncs, Integer> {

    @Resource
    private GroupSyncsMapper mapper;

    @Override
    protected Mapper<GroupSyncs> getMapper() {
        return mapper;
    }

    /**
     * 保存或更新GroupSyncs
     *
     * @param id
     * @param syncInfo
     * @param syncMember
     */
    public void saveOrUpdate(Integer id, Integer syncInfo, Integer syncMember) {

        GroupSyncs groupSyncs = this.getByPrimaryKey(id);
        if (groupSyncs == null) {
            groupSyncs = new GroupSyncs();
            groupSyncs.setGroupId(id);
            if (syncInfo != null) {
                groupSyncs.setSyncInfo(syncInfo);
            }
            if (syncMember != null) {
                groupSyncs.setSyncMember(syncMember);
            }
            this.saveSelective(groupSyncs);
        } else {
            if (syncInfo != null) {
                groupSyncs.setSyncInfo(syncInfo);
            }
            if (syncMember != null) {
                groupSyncs.setSyncMember(syncMember);
            }
            this.updateByPrimaryKeySelective(groupSyncs);
        }
    }
}
