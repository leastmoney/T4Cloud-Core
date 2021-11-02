package com.t4cloud.t.service.job;

import com.t4cloud.t.base.annotation.AutoLog;
import com.t4cloud.t.base.entity.JvmLiveInfo;
import com.t4cloud.t.base.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Date;
import java.util.LinkedList;

import static com.t4cloud.t.base.constant.CacheConstant.SYS_JVM_MONITOR;

/**
 * jvm监控定时存入redis缓存中
 * JvmActuatorJob
 *
 * <p>
 * @return
 * --------------------
 * @author 风平浪静的明天
 * @date 2021/6/16 16:43
 */
@Component
@ConditionalOnProperty(value = "t4cloud.actuator.open", havingValue = "true")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JvmActuatorJob {

    @Value("${spring.application.name}")
    private String application;

    /**
     *  配合上述接口，在redis内保存待获取的信息
     *  一分钟调用一次
     * <p>
     * @return  void
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 16:44
     */
    @AutoLog(value = "Jvm监控任务", logType = 4, operateType = 3)
    @Scheduled(cron = "0 0/1 * * * ?")
    public void saveJvmLiveInfo() {
        //本次时间
        Date now = new Date();
        //获取信息 Jvm内存信息
        MemoryMXBean mxb = ManagementFactory.getMemoryMXBean();
        long jvmMax = 0;
        long jvmUsed = 0;
        //Jvm最大内存 MB
        jvmMax = mxb.getHeapMemoryUsage().getMax() / 1024 / 1024;
        //Jvm最已用内存 MB
        jvmUsed = mxb.getHeapMemoryUsage().getUsed() / 1024 / 1024;

        //获取信息 Jvm线程信息
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        int daemonThreadCount = 0;
        int threadCount = 0;
        //守护线程 条
        daemonThreadCount = threadMXBean.getDaemonThreadCount();
        //活跃线程 条
        threadCount = threadMXBean.getThreadCount();


        //从缓存中获取队列
        LinkedList<JvmLiveInfo> result = (LinkedList<JvmLiveInfo>) RedisUtil.get(SYS_JVM_MONITOR + application);
        if (result == null) {
            result = new LinkedList<>();
        }
        //判断队列是否已经超过60个信息
        if (result.size() >= 60) {
            result.removeFirst();
        }
        result.addLast(new JvmLiveInfo().setJvmMax(jvmMax).setJvmUsed(jvmUsed).setDaemonThreadCount(daemonThreadCount).setThreadCount(threadCount).setTimestamp(now));
        RedisUtil.set(SYS_JVM_MONITOR + application, result, 60 * 60);
    }

}
