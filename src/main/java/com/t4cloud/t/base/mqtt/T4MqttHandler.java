package com.t4cloud.t.base.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 发布消息的回调类
 * <p>
 * 必须实现MqttCallback的接口并实现对应的相关接口方法CallBack 类将实现 MqttCallBack。
 * 每个客户机标识都需要一个回调实例。在此示例中，构造函数传递客户机标识以另存为实例数据。
 * 在回调中，将它用来标识已经启动了该回调的哪个实例。
 * 必须在回调类中实现三个方法：
 * <p>
 * public void messageArrived(MqttTopic topic, MqttMessage message)接收已经预订的发布。
 * <p>
 * public void connectionLost(Throwable cause)在断开连接时调用。
 * <p>
 * public void deliveryComplete(MqttDeliveryToken token))
 * 接收到已经发布的 QoS 1 或 QoS 2 消息的传递令牌时调用。
 * 由 MqttClient.connect 激活此回调。
 */

/**
 * mqtt消息回调
 *
 * <p>
 * --------------------
 *
 * @author TeaR
 * @date 2021/1/18 21:52
 */
@Slf4j
@Service
public abstract class T4MqttHandler implements MqttCallback {

    @Autowired
    private MqttClient client;

    /**
     * 初始化订阅
     *
     * <p>
     *
     * @return void
     * --------------------
     * @author TeaR
     * @date 2021/1/18 21:58
     */
    @PostConstruct
    public void reg() {
        List<String> topicList = topic();
        for (String topic : topicList) {
            client.setCallback(this);
            try {
                client.subscribe(topic);
            } catch (MqttException e) {
            }
        }
    }

    /**
     * 配置订阅列表
     *
     * <p>
     *
     * @return List<String>
     * --------------------
     * @author TeaR
     * @date 2021/1/18 21:58
     */
    public abstract List<String> topic();

    /**
     * 自定义业务处理
     *
     * @param topic 主题
     * @param msg   消息内容
     *              <p>
     * @return void
     * --------------------
     * @author TeaR
     * @date 2021/1/18 21:58
     */
    public abstract void handler(String topic, String msg);


    @Override
    public void connectionLost(Throwable cause) {
        //处理断线重连
        try {
            client.reconnect();
        } catch (MqttException e) {
            log.error("MQTT连接已断开……");
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        //不做处理
        log.debug("deliveryComplete---------" + token.isComplete());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        // subscribe后得到的消息会执行到这里面
        try {
            String msg = new String(message.getPayload());
//        log.debug("接收消息主题 : " + topic);
//        log.debug("接收消息Qos : " + message.getQos());
//        log.debug("接收消息内容 : " + msg);
            handler(topic, msg);
        } catch (Exception e) {
            log.error("MQTT消息处理异常！", e);
            e.printStackTrace();
        }

    }


}