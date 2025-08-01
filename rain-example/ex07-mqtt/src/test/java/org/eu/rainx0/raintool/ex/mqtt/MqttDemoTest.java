package org.eu.rainx0.raintool.ex.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author xiaoyu
 * @time 2025/8/1 15:58
 */
@SpringBootTest
public class MqttDemoTest {

    @Test
    void test1() throws Exception {
        MqttClient client = new MqttClient(
                "tcp://localhost:1883",
                "client_rain",
                new MemoryPersistence() // 持久化策略, 这里是内存方式, 还有文件方式
        );

        MqttConnectOptions options = new MqttConnectOptions();
//        options.setUserName();
//        options.setPassword("<PASSWORD>".toCharArray());
        options.setCleanSession(true);

        client.connect(options);

        System.out.println("连接成功");


//        client.publish("test/1", "hello world".getBytes(), 1, false);
        MqttMessage msg = new MqttMessage();
        msg.setPayload("hello world".getBytes());
        msg.setQos(2);
        client.publish("hi/1", msg);

        client.disconnect();
        client.close();
    }

    @Test
    void test2() throws Exception {
        MqttClient client = new MqttClient("tcp://localhost:1883", "client_rain");

        // 监听方式 1:
//        client.setCallback(new MqttCallback() {
//            @Override
//            public void connectionLost(Throwable throwable) {
//
//            }
//            @Override
//            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
//
//            }
//            @Override
//            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
//
//            }
//        });

        client.connect();

        client.subscribe(
                "test/1", 2,
                // 监听方式 2
                (topic, message) -> {
                    System.out.println("收到消息: " + message.toString());
                }
        );


    }

}
