package com.fisher.newaccountpoc.common.pojo;

import lombok.Getter;
import lombok.Setter;

/**
 * 服务异常
 */
@Getter
public class ServiceException extends RuntimeException{
    /**
     * 自定义异常枚举类
     */
    private ErrorEnum errorEnum;

    /**
     * 错误码
     */
    @Setter
    private Integer code;

    /**
     * 错误信息
     */
    @Setter
    private String errorMsg;


    public ServiceException() {
        super();
    }

    public ServiceException(ErrorEnum errorEnum) {
        super("{code:" + errorEnum.getCode() + ",errorMsg:" + errorEnum.getMsg() + "}");
        this.errorEnum = errorEnum;
        this.code = errorEnum.getCode();
        this.errorMsg = errorEnum.getMsg();
    }

    public ServiceException(Integer code,String errorMsg) {
        super("{code:" + code + ",errorMsg:" + errorMsg + "}");
        this.code = code;
        this.errorMsg = errorMsg;
    }

}
