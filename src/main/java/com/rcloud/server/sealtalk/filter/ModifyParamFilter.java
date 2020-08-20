package com.rcloud.server.sealtalk.filter;

import com.rcloud.server.sealtalk.util.MiscUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Jianlu.Yu
 * @Date: 2020/8/19
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@WebFilter(urlPatterns = "/*", filterName = "modifyParamFilter")
public class ModifyParamFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        ModifyParamRequestWrapper requestWrapper = new ModifyParamRequestWrapper((HttpServletRequest) request);

        Map<String, String[]> parameterMap = new HashMap<>(requestWrapper.getParameterMap());
        Map<String, String[]> iteratorMap = new HashMap<>(parameterMap);

        Set<Map.Entry<String, String[]>> entrySet = iteratorMap.entrySet();
        for (Map.Entry<String, String[]> entry : entrySet) {
            String key = entry.getKey();
            String[] val = entry.getValue();
            if (key.endsWith("Id") || key.endsWith("Ids")) {
                String encodeKey = "encoded" + key.substring(0, 1).toUpperCase() + key.substring(1);
                parameterMap.put(encodeKey, val);
                try {
                    parameterMap.put(key, MiscUtils.decodeIds(val));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        requestWrapper.setParameterMap(parameterMap);
        chain.doFilter(requestWrapper, response);
    }

}
