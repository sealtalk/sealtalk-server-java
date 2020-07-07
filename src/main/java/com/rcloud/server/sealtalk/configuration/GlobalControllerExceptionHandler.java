package com.rcloud.server.sealtalk.configuration;

import com.rcloud.server.sealtalk.constant.ErrorCode;
import com.rcloud.server.sealtalk.exception.ServiceException;
import com.rcloud.server.sealtalk.model.response.Response;
import com.rcloud.server.sealtalk.model.response.ResultWrap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * @Author: xiuwei.nie
 * @Date: 2020/7/7
 * @Description:
 * @Copyright (c) 2020, rongcloud.cn All Rights Reserved
 */
@RestController
@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(value = ServiceException.class)
    public Response serviceAPIExceptionHandler(HttpServletRequest request,
        ServiceException e) {
        String url = request.getRequestURI();
        log.error("Error found: url:[{}]", url, e);
        return ResultWrap.error(e);
    }

    /**
     * ValidLocation 验证参数异常
     */
    @ExceptionHandler(value = BindException.class)
    public Response bindExceptionHandler(HttpServletRequest request, BindException e) {
        log.error("Error found:", e);
        BindingResult bindingResult = e.getBindingResult();
        List<ObjectError> objectErrors = bindingResult.getAllErrors();
        ObjectError objectError = objectErrors.get(0);
        String errorMsg = objectError.getDefaultMessage();
        return ResultWrap.error(ErrorCode.PARAM_ERROR.getErrorCode(), errorMsg);
    }

    /**
     * 参数类型不匹配异常
     */
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public Response methodArgumentTypeExceptionHandler(HttpServletRequest request,
        MethodArgumentTypeMismatchException e) {
        log.error("Error found:", e);
        String parameter = e.getName();
        String errorMsg = String.format("Argument %s type mismatch!", parameter);
        return ResultWrap.error(ErrorCode.PARAM_ERROR.getErrorCode(), errorMsg);
    }

    /**
     * 参数必传异常
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public Response missingServletRequestParameterExceptionHandler(
        HttpServletRequest request, MissingServletRequestParameterException e) {
        log.error("Error found:", e);
        String parameter = e.getParameterName();
        String errorMsg = String.format("The parameter %s is required.", parameter);
        return ResultWrap.error(ErrorCode.PARAM_ERROR.getErrorCode(), errorMsg);
    }
}
