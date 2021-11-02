package com.t4cloud.t.base.redis.topic;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t4cloud.t.base.exception.T4CloudException;
import com.t4cloud.t.base.redis.topic.entity.RedisChannel;
import com.t4cloud.t.base.redis.topic.entity.RedisChannelEnums;
import com.t4cloud.t.base.redis.topic.entity.RedisMsg;
import com.t4cloud.t.base.utils.DynamicEnumUtil;
import com.t4cloud.t.base.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public abstract class T4RedisMsgHandler<T> {

    /**
     * 初始化订阅
     *
     * <p>
     *
     * @author 风平浪静的明天
     * @date 2021/6/22 16:37
     */
    @PostConstruct
    public void init() {
        //获取Redis队列枚举对象
        RedisChannel entity = topic();
        //一个接收者可能有多个通道
        String[] channels = entity.getChannel();
        for (int i = 0; i < channels.length; i++) {
            //增加枚举
            DynamicEnumUtil.addEnum(RedisChannelEnums.class, UUIDUtil.generate(), new Class[]{
                    String.class, Class.class
            }, new Object[]{
                    channels[i], entity.getClazz()
            });
        }

        log.debug(String.format("redis topic register success! class:{%s} ,topic:{%s}", entity.getClazz(), java.util.Arrays.toString(channels)));
    }

    /**
     * 生成配置监听的频道
     *
     * @param topics 需要订阅的topic
     *               <p>
     * @return com.t4cloud.t.base.redis.topic.entity.RedisEnumEntity
     * --------------------
     * @author TeaR
     * @date 2021/7/16 2:34 下午
     */
    protected RedisChannel gen(String... topics) {
        RedisChannel entity = new RedisChannel();
        entity.setClazz(this.getClass());
        entity.setChannel(topics);
        return entity;
    }


    /**
     * 接收消息
     *
     * @param message 消息体(json字符)
     */
    public void receiveMessage(String message) {
        try {
            //将json格式字符串处理为 java对象
            Jackson2JsonRedisSerializer msg = new Jackson2JsonRedisSerializer(RedisMsg.class);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
            msg.setObjectMapper(objectMapper);
            RedisMsg<T> json = (RedisMsg<T>) msg.deserialize(message.getBytes());
            handleMessage(json);
        } catch (Exception e) {
            throw new T4CloudException("Redis队列解析message出错");
        }
    }

    /**
     * 生成配置监听的频道
     */
    public abstract RedisChannel topic();

    /**
     * 对外提供的消息,让子类实现
     *
     * @param msg 自定义Redis队列枚举对象，需要有通道和服务实现类
     */
    public abstract void handleMessage(RedisMsg<T> msg);

}