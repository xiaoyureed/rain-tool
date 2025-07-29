package org.eu.rainx0.raintool.ex3.mq.rocketmq.simple.raw.quickstart;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

/**
 * @author xiaoyu
 * @time 2025/7/17 19:25
 */
public class Producer {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("hah");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        for (int i = 0; i < 2; i++) {
            /**
             * topic 消息的一级分类 (required)
             *      可预先在配置文件中配置预先创建, 亦可发送消息时再指定创建
             *      一般用于区分不同业务
             *      一般微服务中, 一个 service 创建一个 topic, 内部再通过不同 tags 区分
             * tag 消息的二级分类 (optional)
             *      区分某个业务下的不同子类型,
             *      consumer 接受时, 可高效过滤
             * keys 业务 Id, 可选, 方便人工设置一个唯一 Id
             *      系统接受消息后, 会设置一个唯一 msgId , 在 consumer 中可获取
             * flag
             */
            Message msg = new Message("topic1", ("hi" + i).getBytes(StandardCharsets.UTF_8));
            //指定 tag
            // new Message("topic1", "tag1", "keys1", "hello world".getBytes());
            //设置自定义属性, consumer 可根据 sql表达式过滤 (较 tag 灵活, 但是有性能开销), Broker 配置: enablePropertyFilter=true
            //RocketMQ 不支持 消息体内容（Body）过滤，
            //  因为那样就无法在 Broker 端过滤了，只能到达 Consumer 才解析，违背了初衷。
            // msg.putUserProperty("a", "5");

            //延迟消息
            //系统默认定义了一系列延迟级别, 不同延迟级别, 代表不同延迟时间
            //(内部是定义了一系列队列, 每个队列代表不同的延迟级别, 到期后再转发到目标 Topic 中供消费者消费)
            // msg.setDelayTimeLevel(3);
            //自由指定延迟时间 (时间轮实现)
            // msg.setDelayTimeMs(System.currentTimeMillis() + 10 * 1000); // 10s 后发送
            // msg.setDelayTimeSec(10); // 10s 后发送

            /**
             * 单向发送, 不需要等待应答, 不管是否发送成功
             */
            // producer.sendOneway(msg);
            /**
             * 安全性好, 但等待相应过程中是阻塞的, 可设置 timeout
             */
            SendResult sent = producer.send(msg, 10 * 1000); // timeout 10s
            if (sent.getSendStatus() == SendStatus.SEND_OK) {
                System.out.println("msg sent successfully");
            }
            /**
             * 通过 callback 实现异步发送
             */
            // producer.send(msg, new SendCallback() {
            //     @Override
            //     public void onSuccess(SendResult sendResult) {
            //
            //     }
            //     @Override
            //     public void onException(Throwable throwable) {
            //
            //     }
            // });

            //保证消息顺序, MessageQueueSelector + MessageListenerOrderly
            //局部有序 (同个 messageQueue 内有序)
            int orderNo = 0;
            producer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> list, Message message, Object arg) {
                    int order = (int) arg;
                    int index = order % list.size();  // 同一订单可能有多个步骤子消息, 保证子消息进入同一队列, 有序
                    return list.get(index);
                }
            }, orderNo);



            System.out.println(sent);
        }

        System.out.println("msg sent");
        producer.shutdown();
    }
}
