package org.eu.rainx0.raintool.ex3.mq.rocketmq.simple.raw.quickstart;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.MessageSelector;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xiaoyu
 * @time 2025/7/18 09:17
 */
@Slf4j
public class Consumer {
    public static void main(String[] args) throws Exception {
        /**
         * push 模式: 通过在一个死循环中不断轮询, 获取消息并消费
         * pull 模式: 手动获取消息
         */
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("hoo");

        consumer.setNamesrvAddr("localhost:9876");

        /**
         * CONSUME_FROM_LAST_OFFSET (default) 从该消费者组最后消费的偏移量（offset）开始消费
         *      若 messageQueue 中最低点位是 0, 证明这个队列中消息还未被消费过, 依然从 0 开发消费而不是从最新消息开始消费
         * CONSUME_FROM_FIRST_OFFSET 从队列的最开始（第一个消息）开始消费。适用于需要从头消费所有历史消息的场景。
         * CONSUME_FROM_TIMESTAMP 适用于需要按时间点回溯消息的场景，
         *      需要配合 setConsumeTimestamp() 方法使用
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        /**
         * CLUSTERING 集群模式 (default)
         *  同一个 consumer group 下的多个 consumer, 共用messageQueue 中的offset (即两个 consumer不会重复消费某条消息)
         *  可将 consumer group 视为一个逻辑 consumer, 多个逻辑 consumer 还是广播模式
         *
         * BROADCASTING 广播模式
         *  offset 在 consumer 中各自维护自己的 offset,
         *
         */
        consumer.setMessageModel(MessageModel.BROADCASTING);

        /**
         * subExpression
         * - "*" 订阅所有 Tag 的消息
         * - "tag1" 只消费指定 topic中 Tag1 标签的消息
         * -  "TagA||TagB||TagC" 消费多个 tag消息
         */
        consumer.subscribe("topic1", "*");
        // consumer.subscribe("topic1", MessageSelector.byTag());
        /**
         * 支持复杂条件过滤（如 a > 5 AND b = 'abc'）
         */
        // consumer.subscribe("topic1", MessageSelector.bySql());

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(
                List<MessageExt> list,
                ConsumeConcurrentlyContext context
            ) {
                // 打印:
                //Thread [ConsumeMessageThread_hoo_1] got msg:
                // [MessageExt [
                //  brokerName=broker-a,
                //  queueId=2, storeSize=238, queueOffset=0, sysFlag=0, bornTimestamp=1752801294673,
                //  bornHost=/192.168.65.1:47825, storeTimestamp=1752801294676,storeHost=/192.168.65.254:10911,
                //  msgId=C0A841FE00002A9F00000000000000EE, commitLogOffset=238, bodyCRC=1550095198,
                //  reconsumeTimes=0, preparedTransactionOffset=0,
                //  toString()=Message{
                //      topic='topic1', flag=0,
                //      properties={
                //          MIN_OFFSET=0, TRACE_ON=true, MAX_OFFSET=1, MSG_REGION=DefaultRegion,
                //          CONSUME_START_TIME=1752804488457,
                //          UNIQ_KEY=FDA7A32738A4EBE6149533D75669221F3189251A69D7598825510001,
                //          CLUSTER=DefaultCluster, WAIT=true
                //      },
                //      body=[104, 105, 49], transactionId='null'
                //   }
                // ]]
                log.info("Thread [{}] got msg: {}", Thread.currentThread().getName(), list);


                /**
                 * CONSUME_SUCCESS, 成功消费
                 * RECONSUME_LATER; 跳过这条 msg, 接着消费后一条 msg, 等后续再消费这条不成功消息
                 */
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }

        });

        // 配合 MessageQueueSelector 实现消息有序
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {

                /**
                 * SUCCESS, 成功消费
                 * SUSPEND_CURRENT_QUEUE_A_MOMENT 整个 messageQueue 在不成功的 msg 处暂停, 后续再次从该消息处开始消费
                 */
                return ConsumeOrderlyStatus.SUCCESS;
            }
        });


        consumer.start();

        System.out.println("Consumer listener Started.");

    }
}
