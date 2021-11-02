package com.t4cloud.t.base.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * Jvm信息
 *
 * <p>
 *
 * @author 风平浪静的明天
 * @return --------------------
 * @date 2021/6/16 16:43
 */
@Data
@Accessors(chain = true)
public class JvmLiveInfo implements Serializable {

    protected static final long serialVersionUID = 1L;

    //jvm最大内存
    private Long jvmMax;
    //jvm已用内存
    private Long jvmUsed;

    //jvm守护线程
    private int daemonThreadCount;

    //jvm活跃线程
    private int threadCount;

    //时间戳
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;
}
