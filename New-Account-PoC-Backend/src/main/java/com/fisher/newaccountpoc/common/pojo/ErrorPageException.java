package com.fisher.newaccountpoc.common.pojo;

public class ErrorPageException extends ServiceException{
    public ErrorPageException(Integer code,String msg) {
        super(code, msg);
    }
}
