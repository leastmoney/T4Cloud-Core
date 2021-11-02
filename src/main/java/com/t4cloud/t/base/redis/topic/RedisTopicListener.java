package com.t4cloud.t.base.redis.topic;

import com.alibaba.druid.util.StringUtils;
import com.t4cloud.t.base.redis.topic.entity.RedisChannelEnums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * RedisTopic的订阅者配置
 *
 * <p>
 * @author 风平浪静的明天
 * @date 2021/6/22 11:13
 */
@Service
@Configuration
@ConditionalOnProperty(value = "t4cloud.redis-topic.open", havingValue = "true")
public class RedisTopicListener {
    /**
     * 存放策略实例
     * classInstanceMap : key-beanName value-对应的策略实现
     */
    private ConcurrentHashMap<String, T4RedisMsgHandler> classInstanceMap = new ConcurrentHashMap<>();

    /**
     * 注入所有实现了T4RedisMsgHandler接口的Bean
     *
     * @param strategyMap
     *         策略集合
     */
    @Autowired
    public RedisTopicListener(Map<String, T4RedisMsgHandler> strategyMap) {
        this.classInstanceMap.clear();
        strategyMap.forEach((k, v) ->
                this.classInstanceMap.put(k.toLowerCase(), v)
        );
    }


    /**
     * Redis消息监听器容器
     *
     * @param connectionFactory
     *
     * @return
     */
    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        RedisChannelEnums[] redisChannelEnums = RedisChannelEnums.values();
        if (redisChannelEnums.length > 0) {
            for (RedisChannelEnums redisChannelEnum : redisChannelEnums) {
                if (redisChannelEnum == null || StringUtils.isEmpty(redisChannelEnum.getChannel()) || redisChannelEnum.getClassName()==null) {
                    continue;
                }
                //订阅了一个叫pmp和channel 的通道，多通道
                //一个订阅者接收一个频道信息，新增订阅者需要新增RedisChannelEnums定义+BaseSub的子类

                String toLowerCase = redisChannelEnum.getClassName().getSimpleName().toLowerCase();
                T4RedisMsgHandler baseSub = classInstanceMap.get(toLowerCase);
                container.addMessageListener(listenerAdapter(baseSub), new PatternTopic(redisChannelEnum.getChannel()));
            }
        }
        return container;
    }

    /**
     * 配置消息接收处理类
     *
     * @param redisMsgHandler
     *         自定义消息接收类
     *
     * @return MessageListenerAdapter
     */
    @Bean()
    @Scope("prototype")
    MessageListenerAdapter listenerAdapter(T4RedisMsgHandler redisMsgHandler) {
        //这个地方 是给messageListenerAdapter 传入一个消息接受的处理器，利用反射的方法调用“receiveMessage”
        //也有好几个重载方法，这边默认调用处理器的方法 叫handleMessage 可以自己到源码里面看
        //注意2个通道调用的方法都要为receiveMessage
        return new MessageListenerAdapter(redisMsgHandler, "receiveMessage");
    }

}