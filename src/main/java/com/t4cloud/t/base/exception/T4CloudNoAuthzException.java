package com.t4cloud.t.base.exception;

/**
 * 用户鉴权异常
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/10/15 10:22
 */
public class T4CloudNoAuthzException extends T4CloudException {
    private static final long serialVersionUID = 1L;

    public T4CloudNoAuthzException(String message) {
        super(message);
    }

    public T4CloudNoAuthzException(Throwable cause) {
        super(cause);
    }

    public T4CloudNoAuthzException(String message, Throwable cause) {
        super(message, cause);
    }
}
