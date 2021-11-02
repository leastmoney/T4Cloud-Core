package com.t4cloud.t.base.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * redis实时信息
 *
 * <p>
 *
 * @author TeaR
 * @return --------------------
 * @date 2020/2/17 20:18
 */
@Data
@Accessors(chain = true)
public class RedisLiveInfo implements Serializable {

    protected static final long serialVersionUID = 1L;

    private Long keySize;
    private Double memory;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;

}
