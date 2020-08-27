package com.rcloud.server.sealtalk.interceptor;

import com.github.pagehelper.util.StringUtil;
import com.rcloud.server.sealtalk.configuration.SealtalkConfig;
import com.rcloud.server.sealtalk.constant.Constants;
import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.model.RequestUriInfo;
import com.rcloud.server.sealtalk.model.ServerApiParams;
import com.rcloud.server.sealtalk.util.AES256;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author: xiuwei.nie
 * @Author: Jianlu.Yu
 * @Date: 2020/7/6
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 * <p>
 * 拦截器应用场景
 * 1.日志记录：记录请求信息的日志，以便进行信息监控、信息统计、计算PV（Page View）等；
 * 2.登录鉴权：如登录检测，进入处理器检测检测是否登录；
 * 3.性能监控：检测方法的执行时间；
 * 4.其他通用行为
 * <p>
 * 拦截器与 Filter 过滤器的区别
 * 1.拦截器是基于java的反射机制的，而过滤器是基于函数回调。
 * 2.拦截器不依赖于servlet容器，而过滤器依赖于servlet容器。
 * 3.拦截器只能对Controller请求起作用，而过滤器则可以对几乎所有的请求起作用。
 * 4.拦截器可以访问action上下文、值栈里的对象，而过滤器不能访问。
 * 5.在Controller的生命周期中，拦截器可以多次被调用，而过滤器只能在容器初始化时被调用一次。
 * 6.拦截器可以获取IOC容器中的各个bean，而过滤器不行。这点很重要，在拦截器里注入一个service，可以调用业务逻辑。
 */
@Component
@Slf4j
public class RequestInterceptor implements HandlerInterceptor {

    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String UNKNOWN = "unknown";
    public static final String PROXY_CLIENT_IP = "Proxy-Client-IP";
    public static final String WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";
    public static final String HTTP_CLIENT_IP = "HTTP_CLIENT_IP";
    public static final String HTTP_X_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR";

    /**
     * 排除auth认证的url的集合
     */
    public static final Set<String> excludeUrlSet = new CopyOnWriteArraySet<>();

    @Resource
    private SealtalkConfig sealtalkConfig;

    @PostConstruct
    public void postConstruct() {
        String excludeUrls = sealtalkConfig.getExcludeUrl();
        if (!StringUtils.isEmpty(excludeUrls)) {
            String[] excludeUrlArray = excludeUrls.split(",");
            for (String excludeUrl : excludeUrlArray) {
                excludeUrlSet.add(excludeUrl.trim());
            }
        }
    }

    // 进行逻辑判断，如果ok就返回true，不行就返回false，返回false就不会处理请求
    // preHandle：在业务处理器处理请求之前被调用。预处理，可以进行编码、安全控制、权限校验等处理；
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        ServerApiParams serverApiParams = new ServerApiParams();
        serverApiParams.setTraceId(UUID.randomUUID().toString());
        String uri = request.getRequestURI();
        log.info("preHandle uri="+uri);

        RequestUriInfo requestUriInfo = getRequestUriInfo(request);
        log.info("preHandle requestUriInfo: ip={}, remoteAddress={},uri={}", requestUriInfo.getIp(), requestUriInfo.getRemoteAddress(), requestUriInfo.getUri());
        serverApiParams.setRequestUriInfo(requestUriInfo);

        if (!excludeUrlSet.contains(uri)) {
            //不在排除auth认证的url，需要进行身份认证
            Cookie authCookie = getAuthCookie(request);
            if (authCookie == null) {
                response.setStatus(403);
                response.getWriter().write("Not loged in.");
                return false;
            }

            Integer currentUserId = null;
            try {
                currentUserId = getCurrentUserId(authCookie);
                log.info("preHandle currentUserId:" + currentUserId);
                serverApiParams.setCurrentUserId(currentUserId);
            } catch (Exception e) {
                log.error("获取currentUserId异常,error: " + e.getMessage(), e);
            }

            if (currentUserId == null) {
                response.setStatus(500);
                response.getWriter().write("Invalid cookie value");
                return false;
            }
        }
        ServerApiParamHolder.put(serverApiParams);
        return true;
    }

    // afterCompletion：在 DispatcherServlet 完全处理完请求后被调用，可用于清理资源等。
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ServerApiParamHolder.remove();
    }

    private Integer getCurrentUserId(Cookie authCookie) throws ServiceException {
        String cookieValue = authCookie.getValue();
        String decrypt = AES256.decrypt(cookieValue.getBytes(), sealtalkConfig.getAuthCookieKey());
        assert decrypt != null;
        String[] split = decrypt.split(Constants.SEPARATOR_ESCAPE);
        if (split.length != 3) {
            throw new ServiceException(ErrorCode.COOKIE_ERROR, "Invalid cookie value!");
        }
        return Integer.parseInt(split[1]);
    }

    private Cookie getAuthCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                log.info("getAuthCookie " + cookie.getName() + "=" + cookie.getValue());
                if (cookie.getName().equals(sealtalkConfig.getAuthCookieName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    protected RequestUriInfo getRequestUriInfo(HttpServletRequest request) {
        String ip = getIpAddress(request);
        ip = StringUtil.isEmpty(ip) ? "" : ip;
        String uri = request.getRequestURI();
        String remoteAddress = request.getRemoteAddr();
        RequestUriInfo requestUriInfo = new RequestUriInfo();
        requestUriInfo.setUri(uri);
        requestUriInfo.setRemoteAddress(remoteAddress);
        requestUriInfo.setIp(ip);
        return requestUriInfo;
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader(X_FORWARDED_FOR);
        if (StringUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(PROXY_CLIENT_IP);
        }
        if (StringUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(WL_PROXY_CLIENT_IP);
        }
        if (StringUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(HTTP_CLIENT_IP);
        }
        if (StringUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader(HTTP_X_FORWARDED_FOR);
        }
        if (StringUtil.isEmpty(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
