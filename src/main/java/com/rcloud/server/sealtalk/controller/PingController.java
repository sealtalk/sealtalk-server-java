package com.rcloud.server.sealtalk.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.domain.Groups;
import com.rcloud.server.sealtalk.model.dto.DemoSquareDTO;
import com.rcloud.server.sealtalk.model.response.APIResult;
import com.rcloud.server.sealtalk.model.response.APIResultWrap;
import com.rcloud.server.sealtalk.util.MiscUtils;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.util.IOUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/20
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@RestController
@RequestMapping("/")
@Timed(percentiles = {0.9, 0.95, 0.99})
public class PingController {

    @ApiOperation(value = "Ping")
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public APIResult<?> ping() {
        return APIResultWrap.ok("");
    }
}
