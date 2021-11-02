package com.t4cloud.t.base.utils;

import com.t4cloud.t.base.redis.topic.entity.RedisMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * RedisTopicUtil reids消息队列工具类
 *
 * <p>
 * --------------------
 * @author 风平浪静的明天
 * @date 2021/6/21 10:42
 */
@Slf4j
@Component
public class RedisTopicUtil {

    public static RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplateAuto;

    /**
     * 发送消息
     *
     * @param channel  频道
     * @param msg 自定义消息体
     * @return
     */
        public static void sendMessage(String channel, RedisMsg msg) {
            if(channel ==null || msg ==null){
                return;
            }
            redisTemplate.convertAndSend(channel, msg);
        }


    //在Autowired注解之后注入   Constructor(构造方法) -> @Autowired(依赖注入) -> @PostConstruct(注释的方法)
    @PostConstruct
    public void readConfig() {
        redisTemplate = redisTemplateAuto;
    }
}


