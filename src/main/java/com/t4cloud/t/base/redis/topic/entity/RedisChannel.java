package com.t4cloud.t.base.redis.topic.entity;

import lombok.Data;

/**
 * 构建RedisTopic队列枚举实体类
 *
 * <p>
 *
 * @author 风平浪静的明天
 * @date 2021/6/22 11:12
 */
@Data
public class RedisChannel {

    //通道名
    private String[] Channel;

    //服务类名字
    private Class clazz;

}
