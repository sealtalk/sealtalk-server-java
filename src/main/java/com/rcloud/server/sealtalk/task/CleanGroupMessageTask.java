package com.rcloud.server.sealtalk.task;

import com.rcloud.server.sealtalk.manager.GroupManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/28
 * @Description: 清理群历史消息定时任务
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */

@Slf4j
@Component
public class CleanGroupMessageTask {

    @Autowired
    private GroupManager groupManager;

    //0 0 */1 * * ? cron表达式每1小时执行1次
    @Scheduled(cron = "0 0 */1 * * ? ")
    public void clearGroupMessageTask() {

        try {
            log.info("CleanGroupMessageTask begin.");
            groupManager.cleanGroupMessage();
            log.info("CleanGroupMessageTask end.");
        } catch (Exception e) {
            log.error("CleanGroupMessageTask execute error:" + e.getMessage(), e);
        }
    }
}
