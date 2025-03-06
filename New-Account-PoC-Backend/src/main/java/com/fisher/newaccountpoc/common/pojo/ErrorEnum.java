package com.fisher.newaccountpoc.common.pojo;

import lombok.Getter;

/**
 * error code enum
 */

@Getter
public enum ErrorEnum {
    //自定义系列
    USER_NAME_IS_NOT_NULL(10001,"【Param validation】use name not null"),
    PWD_IS_NOT_NULL(10002,"【Param validation】password not null"),

    //400系列
    BAD_REQUEST(400,"The requested data format does not match! - 400"),
    UNAUTHORIZED(401,"unauthorized! - 401"),
    FORBIDDEN(403,"no access to request! - 403"),
    NOT_FOUND(404, "resource not found! - 404"),

    //500系列
    INTERNAL_SERVER_ERROR(500, "internal error! - 500"),
    SERVICE_UNAVAILABLE(503,"server is busy! - 503"),

    //未知异常
    FAIL(10000,"operation failed! - 10000"),
    FAILED_UPDATE(10002,"update failed! - 10002"),
    INVALID_STATUS(10001,"invalid status! - 10001");


    /** 错误码 */
    private Integer code;

    /** 错误描述 */
    private String msg;

    ErrorEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
