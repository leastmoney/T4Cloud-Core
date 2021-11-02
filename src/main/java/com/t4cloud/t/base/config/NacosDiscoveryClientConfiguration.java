package com.t4cloud.t.base.config;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.discovery.NacosWatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * nacos客户端注册至服务端时，自定义元数据
 *
 * --------------------
 * @author TeaR
 * @date 2021/5/25 18:56
 */
@Slf4j
@Configuration
@ConditionalOnNacosDiscoveryEnabled
@AutoConfigureBefore({SimpleDiscoveryClientAutoConfiguration.class, CommonsClientAutoConfiguration.class})
public class NacosDiscoveryClientConfiguration {

    @Autowired(required = false)
    private BuildProperties buildProperties;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = {"spring.cloud.nacos.discovery.watch.enabled"}, matchIfMissing = true)
    public NacosWatch nacosWatch(NacosDiscoveryProperties nacosDiscoveryProperties, WebApplicationContext webApplicationContext) {
        //注册应用版本信息
        String version = buildProperties == null ? "unknown" : buildProperties.getVersion();
        nacosDiscoveryProperties.getMetadata().put("version", version);

        //更改服务详情中的元数据，增加服务注册时间
        nacosDiscoveryProperties.getMetadata().put("startup.time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        //增加机器名
        try {
            nacosDiscoveryProperties.getMetadata().put("client.name", InetAddress.getLocalHost().getHostName().toString());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return new NacosWatch(nacosDiscoveryProperties);
    }

}