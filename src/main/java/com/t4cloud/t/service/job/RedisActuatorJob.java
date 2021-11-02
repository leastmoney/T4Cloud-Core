package com.t4cloud.t.service.job;

import com.t4cloud.t.base.annotation.AutoLog;
import com.t4cloud.t.base.entity.RedisLiveInfo;
import com.t4cloud.t.base.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

import static com.t4cloud.t.base.constant.CacheConstant.SYS_REDIS_MONITOR;

/**
 * RedisActuatorJob
 * <p>
 * ---------------------
 *
 * @author Terry
 * @date 2020/3/20 15:37
 */
@Component
@ConditionalOnProperty(value = "t4cloud.actuator.open", havingValue = "true")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedisActuatorJob {

    private final RedisConnectionFactory redisConnectionFactory;
    @Value("${spring.application.name}")
    private String application;

    /**
     * 配合上述接口，在redis内保存待获取的信息
     * 一分钟调用一次
     *
     * @return void
     * --------------------
     * @author TeaR
     * @date 2020/2/17 21:54
     */
    @AutoLog(value = "Redis监控任务", logType = 4, operateType = 3)
    @Scheduled(cron = "0 0/1 * * * ?")
    public void saveRedisLiveInfo() {
        //本次时间
        Date now = new Date();
        //获取关键信息
        Long dbSize = redisConnectionFactory.getConnection().dbSize();
        //获取使用的缓存
        Double memory = 0.0;
        Properties info = redisConnectionFactory.getConnection().info();
        for (Map.Entry<Object, Object> entry : info.entrySet()) {
            String key = StringUtils.trim(entry.getKey() + "");
            if ("used_memory_rss_human".equals(key)) {
                String value = entry.getValue().toString();
                if (value.contains("M")) {
                    memory = Double.valueOf(value.replace("M", ""));
                }

                if (value.contains("K")) {
                    memory = Double.valueOf(value.replace("K", "")) / 1024;
                }
            }
        }
        //从缓存中获取队列
        LinkedList<RedisLiveInfo> result = (LinkedList<RedisLiveInfo>) RedisUtil.get(SYS_REDIS_MONITOR + application);
        if (result == null) {
            result = new LinkedList<>();
        }
        //判断队列是否已经超过60个信息
        if (result.size() >= 60) {
            result.removeFirst();
        }
        result.addLast(new RedisLiveInfo().setKeySize(dbSize).setMemory(memory).setTimestamp(now));
        RedisUtil.set(SYS_REDIS_MONITOR + application, result, 60 * 60);
    }

}
