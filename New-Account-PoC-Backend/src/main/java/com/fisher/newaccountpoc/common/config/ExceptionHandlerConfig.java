package com.fisher.newaccountpoc.common.config;

import com.fisher.newaccountpoc.common.pojo.ErrorEnum;
import com.fisher.newaccountpoc.common.pojo.ErrorPageException;
import com.fisher.newaccountpoc.common.pojo.ServiceException;
import com.fisher.newaccountpoc.common.util.Result;
import com.fisher.newaccountpoc.common.util.ResultUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 异常处理配置
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandlerConfig {
    /**
     * 业务异常 统一处理
     */
    @ExceptionHandler(value = ServiceException.class)
    @ResponseBody
    public Object exceptionHandler400(ServiceException e){
        return returnResult(e, ResultUtil.exception(e));
    }

    /**
     * 错误页面异常 统一处理
     */
    @ExceptionHandler(value = ErrorPageException.class)
    @ResponseBody
    public Object exceptionHandler(ErrorPageException e){
        ErrorEnum errorEnum = switch (e.getCode()) {
            case 404 -> ErrorEnum.NOT_FOUND;
            case 403 -> ErrorEnum.FORBIDDEN;
            case 401 -> ErrorEnum.UNAUTHORIZED;
            case 400 -> ErrorEnum.BAD_REQUEST;
            default -> ErrorEnum.FAIL;
        };

        return returnResult(e,ResultUtil.exception(errorEnum));
    }

    /**
     * 空指针异常 统一处理
     */
    @ExceptionHandler(value =NullPointerException.class)
    @ResponseBody
    public Object exceptionHandler500(NullPointerException e){
        return returnResult(e,ResultUtil.exception(ErrorEnum.INTERNAL_SERVER_ERROR));
    }

    /**
     * 其他异常 统一处理
     */
    @ExceptionHandler(value =Exception.class)
    @ResponseBody
    public Object exceptionHandler(Exception e){
        return returnResult(e,ResultUtil.defineFail(ErrorEnum.FAIL.getCode(), "【" + e.getClass().getName() + "】" + e.getMessage()));
    }

    /**
     * 是否为ajax请求
     * ajax请求，响应json格式数据，否则应该响应html页面
     */
    private Object returnResult(Exception e, Result errorResult){
        //把错误信息输入到日志中
        log.error("发生异常：",e);

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();

        //设置http响应状态
        response.setStatus(200);

        //判断是否为ajax请求
        return errorResult;
    }
}
