package com.t4cloud.t.base.exception;

/**
 * 参数校验异常
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/6/30 10:39
 */
public class T4CloudDecryptException extends T4CloudException {
    private static final long serialVersionUID = 1L;

    public T4CloudDecryptException(String message) {
        super(message);
    }

    public T4CloudDecryptException(Throwable cause) {
        super(cause);
    }

    public T4CloudDecryptException(String message, Throwable cause) {
        super(message, cause);
    }
}
