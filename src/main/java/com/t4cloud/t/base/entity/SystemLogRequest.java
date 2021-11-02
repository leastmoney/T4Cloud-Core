package com.t4cloud.t.base.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 请求数据统计信息
 *
 * <p>
 *
 * @author 风平浪静的明天
 * @return --------------------
 * @date 2021/6/16 16:43
 */
@Data
@Accessors(chain = true)
public class SystemLogRequest implements Serializable {

    protected static final long serialVersionUID = 1L;

    //请求总量
    private Long requestCount;
    //正常请求总量
    private Long normalRequestCount;

    //异常请求总量
    private Long abnormalRequestCount;

    //时间戳
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date timestamp;
}
