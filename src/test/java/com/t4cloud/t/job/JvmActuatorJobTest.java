package com.t4cloud.t.job;

import com.t4cloud.t.T4CloudCoreApplication;
import com.t4cloud.t.base.entity.JvmLiveInfo;
import com.t4cloud.t.base.utils.RedisUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Date;
import java.util.LinkedList;

import static com.t4cloud.t.base.constant.CacheConstant.SYS_JVM_MONITOR;

/**
 * Jvm检测测试
 *
 * <p>
 * @return
 * --------------------
 * @author 风平浪静的明天
 * @date 2021/6/16 10:31
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {T4CloudCoreApplication.class})
public class JvmActuatorJobTest {

    @Value("${spring.application.name}")
    private String application;

    /**
     * JvmActuator测试方法
     *
     * <p>
     * @return
     * --------------------
     * @author 风平浪静的明天
     * @date 2021/6/16 10:31
     */
    @Test
    public void JvmActuatorJobTest(){
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
        boolean set = RedisUtil.set(SYS_JVM_MONITOR + application, result, 60 * 60);
        Assert.assertTrue("存入成功",set);
    }

}
