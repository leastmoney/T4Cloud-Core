package com.t4cloud.t.base.exception;

/**
 * 请求校验异常
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/3/20 14:23
 */
public class T4CloudBadRequestException extends T4CloudException {
    private static final long serialVersionUID = 1L;

    public T4CloudBadRequestException(String message) {
        super(message);
    }

    public T4CloudBadRequestException(Throwable cause) {
        super(cause);
    }

    public T4CloudBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
