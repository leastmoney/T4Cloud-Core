package com.t4cloud.t.service.mapper;


import com.baomidou.mybatisplus.annotation.SqlParser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.t4cloud.t.base.entity.SystemLogRequest;
import com.t4cloud.t.service.entity.SysLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统日志表 Mapper 接口
 *
 * <p>
 * --------------------
 *
 * @author T4Cloud
 * @since 2020-02-11
 */
public interface CommonLogMapper extends BaseMapper<SysLog> {

    /**
     * 统计今日总量的请求
     *
     * @params tartDate 今日开始时间
     * @params endDate 今日结束时间
     * @params tenantIds 租户ids
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/24 1:25
     */
    @SqlParser(filter = true)
    @Select(" SELECT\n" +
            "\tDATE_FORMAT( create_time, '%Y-%m-%d' ) AS TIMESTAMP ,\n" +
            "\tcount(*) AS requestCount\n" +
            "FROM\n" +
            "\tsys_log \n" +
            "WHERE\n" +
            "\tlog_type IN ( '1', '2', '3' ) \n" +
            "\tAND create_time BETWEEN '${startDate}' \n" +
            "\tAND '${endDate}' \n" +
            "\tAND tenant_id IN (${tenantIds}) limit 0,1")
    SystemLogRequest countSystemLog(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("tenantIds") String tenantIds);


    /**
     * 统计今日正常量/异常量的请求
     *
     * @params tartDate 今日开始时间
     * @params endDate 今日结束时间
     * @params tenantIds 租户ids
     * @params resultType 正常异常标识 0-异常，1-正常
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/24 1:25
     */
    @SqlParser(filter = true)
    @Select(" SELECT\n" +
            "\tDATE_FORMAT( create_time, '%Y-%m-%d' ) AS TIMESTAMP ,\n" +
            "\tcount(*) AS requestCount\n" +
            "FROM\n" +
            "\tsys_log \n" +
            "WHERE\n" +
            "\tresult_type = '${resultType}' \n" +
            "\tAND log_type IN ( '1', '2', '3' ) \n" +
            "\tAND create_time BETWEEN '${startDate}' \n" +
            "\tAND '${endDate}' \n" +
            "\tAND tenant_id IN (${tenantIds}) limit 0,1")
    SystemLogRequest countSystemLogWithType(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("tenantIds") String tenantIds,@Param("resultType") String resultType);

    }
