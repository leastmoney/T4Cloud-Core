package com.t4cloud.t.base.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 系统日志表
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class T4Log implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 创建人
     */
    protected String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date createTime;
    /**
     * 更新人
     */
    protected String updateBy;
    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date updateTime;
    private String id;
    /**
     * 日志类型
     */
    private Integer logType;
    /**
     * 操作详细日志
     */
    private String logContent;
    /**
     * 操作类型
     */
    private Integer operateType;
    /**
     * 操作结果
     */
    private String result;
    /**
     * 是否异常（0-异常，1-正常）
     */
    private Integer resultType;
    /**
     * 操作人用户账户
     */
    private String userId;
    /**
     * 操作人用户名称
     */
    private String username;
    /**
     * IP
     */
    private String ip;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 请求路径
     */
    private String requestUrl;
    /**
     * 请求参数
     */
    private String requestParam;
    /**
     * 请求参数
     */
    private String requestHeader;
    /**
     * 请求类型
     */
    private String requestType;
    /**
     * 耗时
     */
    private Long costTime;

}
