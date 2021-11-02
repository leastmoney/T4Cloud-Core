package com.t4cloud.t.base.entity.dto;

import com.t4cloud.t.base.utils.HttpStatusUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * 通用的高复用类
 *
 * <p>
 * 作为通用的返回对象，提供标准格式和快速调用的方法
 * --------------------
 *
 * @author TeaR
 * @date 2020/1/15 13:01
 */
@Data
@ApiModel(value = "通用返回对象", description = "接口返回对象")
@NoArgsConstructor
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 成功标志
     */
    @ApiModelProperty(value = "成功标志")
    private boolean success = true;

    /**
     * 返回处理消息
     */
    @ApiModelProperty(value = "返回处理消息")
    private String message = "操作成功！";

    /**
     * 返回代码
     */
    @ApiModelProperty(value = "返回代码")
    private Integer code = 0;

    /**
     * 返回数据对象 data
     */
    @ApiModelProperty(value = "返回数据对象")
    private T result;

    /**
     * 时间戳
     */
    @ApiModelProperty(value = "时间戳")
    private long timestamp = System.currentTimeMillis();

    // ----------------------------------------------- Function -----------------------------------------------

    public static <T> R<T> ok() {
        return ok(HttpStatus.OK.value(), "成功", null);
    }

    // ----------------------------------------------- 成功 -----------------------------------------------

    public static <T> R<T> ok(String msg) {
        return ok(HttpStatus.OK.value(), msg, null);
    }

    public static <T> R<T> ok(String msg, T data) {
        return ok(HttpStatus.OK.value(), msg, data);
    }

    public static <T> R<T> ok(Integer code, String msg, T data) {
        R<T> r = new R<>();
        r.setMessage(msg);
        r.setSuccess(true);
        r.setCode(code);
        r.setResult(data);
        HttpStatusUtil.setHttpStatus(r);
        return r;
    }

    public static R error(String msg) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
    }

    // ----------------------------------------------- 异常 -----------------------------------------------

    public static R error(int code, String msg) {
        R r = new R();
        r.setCode(code);
        r.setMessage(msg);
        r.setSuccess(false);
        HttpStatusUtil.setHttpStatus(r);
        return r;
    }

    // ----------------------------------------------- 授权异常 -----------------------------------------------

    /**
     * 未登录
     *
     * @param message 异常消息
     * <p>
     * @return com.t4cloud.t.base.entity.dto.R<T>
     * --------------------
     * @author TeaR
     * @date 2021/5/31 11:04
     */
    public static <T> R<T> noLogin(String message) {
        R<T> r = new R<T>();
        r.setCode(HttpStatus.UNAUTHORIZED.value());
        r.setMessage(message);
        r.setSuccess(false);
        HttpStatusUtil.setHttpStatus(r);
        return r;
    }

    /**
     * 权限不足
     *
     * @param message 异常消息
     * <p>
     * @return com.t4cloud.t.base.entity.dto.R<T>
     * --------------------
     * @author TeaR
     * @date 2021/5/31 11:05
     */
    public static <T> R<T> noAuth(String message) {
        R<T> r = new R<T>();
        r.setCode(HttpStatus.FORBIDDEN.value());
        r.setMessage(message);
        r.setSuccess(false);
        HttpStatusUtil.setHttpStatus(r);
        return r;
    }

    public static <T> R<T> noAuth() {
        return noAuth("您的登录已失效！请尝试重新登录。");
    }

    // ----------------------------------------------- 资源异常 -----------------------------------------------

    public static <T> R<T> notFound(String message) {
        R<T> r = new R<T>();
        r.setCode(HttpStatus.NOT_FOUND.value());
        r.setMessage(message);
        r.setSuccess(false);
        HttpStatusUtil.setHttpStatus(r);
        return r;
    }

    public static <T> R<T> notFound() {
        return notFound("找不到该资源。");
    }

    /**
     * 在手动改变了CODE后，刷新一下状态
     * <p>
     * --------------------
     *
     * @author TeaR
     * @date 2020/12/9 16:27
     */
    public void flushHttpStatus() {
        HttpStatusUtil.setHttpStatus(this);
    }
}