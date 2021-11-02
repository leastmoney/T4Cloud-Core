package com.t4cloud.t.service.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.t4cloud.t.base.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 系统日志表 实体类
 *
 * <p>
 * --------------------
 *
 * @author T4Cloud
 * @since 2020-02-11
 */
@Data
@TableName("sys_log")
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "SysLog对象", description = "系统日志表")
public class SysLog extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 日志类型（1-管理员操作，2-登录日志，3-用户操作，4-定时任务，5-其他日志）
     */
    @ApiModelProperty(value = "日志类型（1-管理员操作，2-登录日志，3-用户操作，4-定时任务，5-其他日志）")
    private Integer logType;
    /**
     * 日志内容
     */
    @ApiModelProperty(value = "日志内容")
    private String logContent;
    /**
     * 操作类型(1-增，2-删，3-改，4-查)
     */
    @ApiModelProperty(value = "操作类型(1-增，2-删，3-改，4-查)")
    private Integer operateType;
    /**
     * 操作结果记录
     */
    @ApiModelProperty(value = "操作结果记录")
    private String result;
    /**
     * 是否异常（0-异常，1-正常）
     */
    @ApiModelProperty(value = "操作结果是否异常（0-异常，1-正常）")
    private Integer resultType;
    /**
     * 操作用户账号
     */
    @ApiModelProperty(value = "操作用户账号")
    private String userId;
    /**
     * 操作用户名称
     */
    @ApiModelProperty(value = "操作用户名称")
    private String username;
    /**
     * IP
     */
    @ApiModelProperty(value = "IP")
    private String ip;
    /**
     * 请求java方法
     */
    @ApiModelProperty(value = "请求java方法")
    private String method;
    /**
     * 请求路径
     */
    @ApiModelProperty(value = "请求路径")
    private String requestUrl;
    /**
     * 请求参数
     */
    @ApiModelProperty(value = "请求参数")
    private String requestParam;
    /**
     * 请求参数
     */
    @ApiModelProperty(value = "请求参数")
    private String requestHeader;
    /**
     * 请求类型
     */
    @ApiModelProperty(value = "请求类型")
    private String requestType;
    /**
     * 耗时
     */
    @ApiModelProperty(value = "耗时")
    private Long costTime;
    public SysLog(String id) {
        this.id = id;
    }


}
