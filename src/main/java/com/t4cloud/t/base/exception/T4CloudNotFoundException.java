package com.t4cloud.t.base.exception;

/**
 * 项目自定义异常
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/1/15 16:00
 */
public class T4CloudNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public T4CloudNotFoundException(String message) {
        super(message);
    }

    public T4CloudNotFoundException(Throwable cause) {
        super(cause);
    }

    public T4CloudNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
