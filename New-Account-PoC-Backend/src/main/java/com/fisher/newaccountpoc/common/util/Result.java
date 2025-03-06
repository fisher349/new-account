package com.fisher.newaccountpoc.common.util;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 封装请求返回对象
 * @param <T>
 */
public class Result<T> {
    @Schema(name = "code：200 means success，others means failures")
    private Integer code;
    @Schema(name = "message content")
    private String msg;
    @Schema(name = "response data")
    private T data;

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public Result<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Result<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public Result<T> setCodeByEnum(ResultEnum resultEnum) {
        this.code = resultEnum.code;
        return this;
    }
}
