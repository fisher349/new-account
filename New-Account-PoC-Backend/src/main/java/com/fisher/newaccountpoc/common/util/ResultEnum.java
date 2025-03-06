package com.fisher.newaccountpoc.common.util;

/**
 * return code enum
 */
public enum ResultEnum {
    /**
     * 成功
     */
    SUCCESS(200),

    /**
     * 失败
     */
    FAIL(100),

    /**
     * 接口不存在
     */
    NOT_FOUND(404),
    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500);
    protected Integer code;
    ResultEnum(Integer code) {
        this.code = code;
    }
}
