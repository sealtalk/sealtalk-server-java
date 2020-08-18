package com.rcloud.server.sealtalk.model.dto;

import com.rcloud.server.sealtalk.domain.Groups;
import lombok.Data;

import java.util.List;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/18
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@Data
public class FavGroupsDTO {

    private Integer limit;
    private Integer offset;
    private Integer total;
    private List<Groups> groupsList;
}
