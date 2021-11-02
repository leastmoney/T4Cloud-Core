package com.t4cloud.t.base.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 已登录用户信息
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/1/15 23:29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class LoginUser {

    /**
     * 公司ID（租户ID）
     */
    protected String tenantId;
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
    /**
     * 登录人id
     */
    private String id;
    /**
     * 登录账号
     */
    private String username;
    /**
     * 真实姓名
     */
    private String realname;
    /**
     * 密码
     */
    private String password;
    /**
     * md5密码盐
     */
    private String salt;
    /**
     * 工号，唯一键
     */
    private String workNo;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 生日
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;
    /**
     * 性别(0-默认未知,1-男,2-女)
     */
    private Integer gender;
    /**
     * 电子邮件
     */
    private String email;
    /**
     * 电话
     */
    private String phone;
    /**
     * 职务，关联职务表
     */
    private String post;
    /**
     * 身份证号
     */
    private String idCard;
    /**
     * 住址
     */
    private String address;
    /**
     * 性别(1-正常,2-冻结)
     */
    private Integer status;
    /**
     * token
     */
    private String token;

}
