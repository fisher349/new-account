package com.fisher.newaccountpoc.common.util;

import com.fisher.newaccountpoc.common.pojo.ErrorEnum;
import com.fisher.newaccountpoc.common.pojo.ServiceException;

/**
 * 定义返回对象实例化方法
 */
public class ResultUtil {

    private ResultUtil() {
        throw new IllegalStateException("Utility class");
    }
    public static <T> Result<T>  defineSuccess(Integer code, T data) {
        Result<T> result = new Result<>();
        return result.setCode(code).setData(data);
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCodeByEnum(ResultEnum.SUCCESS).setMsg("success").setData(data);
        return result;
    }
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCodeByEnum(ResultEnum.SUCCESS).setMsg("success");
        return result;
    }

    public static <T> Result<T> fail(String msg) {
        Result<T> result = new Result<>();
        result.setCodeByEnum(ResultEnum.FAIL).setMsg(msg);
        return result;
    }
    public static <T> Result<T> fail() {
        Result<T> result = new Result<>();
        result.setCodeByEnum(ResultEnum.FAIL).setMsg("fail");
        return result;
    }

    public static <T> Result<T> defineFail(int code, String msg){
        Result<T> result = new Result<>();
        result.setCode(code).setMsg(msg);
        return result;
    }

    public static <T> Result<T> define(int code, String msg, T data){
        Result<T> result = new Result<>();
        result.setCode(code).setMsg(msg).setData(data);
        return result;
    }
    public static <T> Result<T> exception(ErrorEnum errorEnum) {
        Result<T> result = new Result<>();
        result.setCode(errorEnum.getCode()).setMsg(errorEnum.getMsg());
        return result;
    }
    public static <T> Result<T> exception(ServiceException e) {
        Result<T> result = new Result<>();
        ErrorEnum errorEnum = e.getErrorEnum();
        if (errorEnum ==null) {
            result.setCode(e.getCode()).setMsg(e.getErrorMsg());
        } else {
            result.setCode(errorEnum.getCode()).setMsg(errorEnum.getMsg());
        }
        return result;
    }
}
