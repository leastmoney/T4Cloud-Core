package com.t4cloud.t.base.exception;

/**
 * 参数校验异常
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/1/15 16:00
 */
public class T4CloudValidException extends T4CloudException {
    private static final long serialVersionUID = 1L;

    public T4CloudValidException(String message) {
        super(message);
    }

    public T4CloudValidException(Throwable cause) {
        super(cause);
    }

    public T4CloudValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
