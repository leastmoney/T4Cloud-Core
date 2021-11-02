package com.t4cloud.t.base.redis.topic.entity;

import lombok.Data;
import java.io.Serializable;

/**
 * RedisTopic 标准消息结构体
 *
 * <p>
 * @author 风平浪静的明天
 * @date 2021/6/22 11:12
 */
@Data
public class RedisMsg<T> implements Serializable {

    protected static final long serialVersionUID = 1L;

    /**
     * 自定义对象
     */
    private T data;

    /**
     * 通道
     */
    private String channel;

    /**
     * 消息体
     */
    private String msg;
}
