package com.t4cloud.t.base.constant;

/**
 * 日志常量
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/1/15 18:04
 */
public interface LogConstant {


    // ----------------------------------------------- 日志类型 -----------------------------------------------

    /**
     * 管理员操作
     */
    int LOG_TYPE_ADMIN = 1;

    /**
     * 登录操作
     */
    int LOG_TYPE_LOGIN = 2;

    /**
     * 用户操作
     */
    int LOG_TYPE_USER = 3;

    /**
     * 定时任务
     */
    int LOG_TYPE_JOB = 4;

    /**
     * 其他操作
     */
    int LOG_TYPE_OTHER = 5;


    // ----------------------------------------------- 操作类型 -----------------------------------------------
    /**
     * 增
     */
    int OP_TYPE_ADD = 1;
    /**
     * 删
     */
    int OP_TYPE_DEL = 2;
    /**
     * 改
     */
    int OP_TYPE_EDIT = 3;
    /**
     * 改
     */
    int OP_TYPE_QUERY = 4;


}
