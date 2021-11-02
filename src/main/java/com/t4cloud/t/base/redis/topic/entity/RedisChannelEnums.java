package com.t4cloud.t.base.redis.topic.entity;

import com.t4cloud.t.base.redis.topic.T4RedisMsgHandler;

/**
 * Redis发布订阅枚举类
 *
 * <p>
 * @author 风平浪静的明天
 * @date 2021/6/22 16:39
 */
public enum RedisChannelEnums {

    /**redis频道名称定义 需要与发布者一致*/
//    LIVE_INFO_CHANGE("chat", LiveChangeSub.class),
//    LIVE_INFO_1("chat", Live11Sub.class),

    ;
    /** 枚举定义+描述 */
    private String channel;
    private Class<? extends T4RedisMsgHandler> className;

    RedisChannelEnums(String channel, Class<? extends T4RedisMsgHandler> className) {
        this.channel = channel;
        this.className=className;
    }


    /** 根据code获取对应的枚举对象 */
    public static RedisChannelEnums getEnum(String channel) {
        RedisChannelEnums[] values = RedisChannelEnums.values();
        if (null != channel && values.length > 0) {
            for (RedisChannelEnums value : values) {
                if (value.channel == channel) {
                    return value;
                }
            }
        }
        return null;
    }

    /** 该code在枚举列表code属性是否存在 */
    public static boolean containsCode(String code) {
        RedisChannelEnums anEnum = getEnum(code);
        return anEnum != null;
    }

    /** 判断code与枚举中的code是否相同 */
    public static boolean equals(String channel, RedisChannelEnums calendarSourceEnum) {
        return calendarSourceEnum.channel == channel;
    }


    public String getChannel() {
        return channel;
    }

    public Class<? extends T4RedisMsgHandler> getClassName() {
        return className;
    }
}
