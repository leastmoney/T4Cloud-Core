package com.t4cloud.t.base.exception;

/**
 * 项目业务自定义异常，携带code标识
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/11/28 15:28
 */
public class T4CloudServiceException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int code;

    public T4CloudServiceException(String message) {
        super(message);
    }

    public T4CloudServiceException(Throwable cause) {
        super(cause);
    }

    public T4CloudServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public T4CloudServiceException(int code, String message) {
        super(message);
        this.code = code;
    }

    public T4CloudServiceException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
