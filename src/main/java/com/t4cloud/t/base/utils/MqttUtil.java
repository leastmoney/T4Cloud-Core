package com.t4cloud.t.base.utils;

import com.t4cloud.t.base.mqtt.T4MqttHandler;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Mqtt 服务端 静态工具类整合
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2021/1/18 21:37
 */
@Component
@ConditionalOnProperty(value = "t4cloud.mqtt.open", havingValue = "true")
public class MqttUtil {

    private static MqttClient client;
    @Autowired
    private MqttClient clientAuto;

    /**
     * 重连方法，将MQTT重新连接
     *
     * <p>
     *
     * @return void
     * --------------------
     * @author TeaR
     * @date 2021/1/18 21:55
     */
    public static void reconnect() throws MqttException {
        if (!client.isConnected()) {
            client.connect();
        } else {
            client.disconnect();
            client.reconnect();
        }
    }

    /**
     * 重连方法，将MQTT重新连接
     *
     * <p>
     *
     * @return void
     * --------------------
     * @author TeaR
     * @date 2021/1/18 21:55
     */
    public static void subscribe(String topic, T4MqttHandler handler) {
        try {
            client.setCallback(handler);
            client.subscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息发送
     *
     * @param topic 主题
     * @param msg   消息内容
     *              <p>
     * @return boolean
     * --------------------
     * @author TeaR
     * @date 2021/1/18 21:56
     */
    public static boolean send(String topic, String msg) {
        MqttMessage message = new MqttMessage();
        message.setQos(2);
        message.setRetained(true);
        message.setPayload(msg.getBytes());
        try {
            client.publish(topic, message);
            return true;
        } catch (MqttException e) {
            //消息发送失败
            return false;
        }
    }

    @PostConstruct
    public void readConfig() {
        client = this.clientAuto;
    }

}
