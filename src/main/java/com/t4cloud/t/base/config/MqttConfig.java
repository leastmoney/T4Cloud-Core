package com.t4cloud.t.base.config;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * ------------------
 * 客户端-----就只提供连接和暴露发布消息的方法
 *
 * @Author mawang
 * @Date 2020/12/23 16:38
 **/
@Data
@Slf4j
@ConditionalOnProperty(value = "t4cloud.mqtt.open", havingValue = "true")
@ConfigurationProperties(prefix = "t4cloud.mqtt")
@Configuration
public class MqttConfig {

    @Value("${t4cloud.mqtt.client-id}")
    private String clientId;
    @Value("${t4cloud.mqtt.host}")
    private String host;
    @Value("${t4cloud.mqtt.user}")
    private String user;
    @Value("${t4cloud.mqtt.password}")
    private String password;

    /**
     * 建立服务器连接
     *
     * @return 返回新建连接的客户端
     * ------------------
     * @Author mawang
     * @Date 2021/1/8 16:34
     **/
    @Bean
    public MqttClient connect() throws MqttException {
        MqttClient client = new MqttClient(host, clientId + System.currentTimeMillis(), new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(user);
        options.setPassword(password.toCharArray());

        options.setCleanSession(false);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(20);
        try {
            client.connect(options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return client;
    }

}