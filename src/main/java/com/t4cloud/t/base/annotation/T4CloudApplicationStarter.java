package com.t4cloud.t.base.annotation;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.annotation.*;

/**
 * T4CLOUD 启动注解
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2020/12/7 22:27
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SpringBootApplication(scanBasePackages = {"com.t4cloud.t"})
@EnableDiscoveryClient
//如果不需要nacos服务治理
//@EnableDiscoveryClient(autoRegister = false)
@ComponentScan(basePackages = {"com.t4cloud.t", "cn.hutool.extra.spring"})
@EnableScheduling
@EnableFeignClients(basePackages = "com.t4cloud.t")
public @interface T4CloudApplicationStarter {
}