package org.eu.rainx0.raintool.ex3.mq.rocketmq.simple.raw.transaction;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import lombok.SneakyThrows;

/**
 * “发送消息” 与 “本地数据库操作” 需要同时成功或失败的业务场景
 *
 * @author xiaoyu
 * @time 2025/7/18 14:00
 */
public class Producer {
    /**
     *
     * 1. Producer 发送 “半消息”（暂不对消费者可见）
     * 2. 执行本地事务（如写数据库）
     * 3. 提交本地事务后：
     *     - 如果成功 → 通知 Broker “提交消息”
     *     - 如果失败 → 通知 Broker “回滚消息”
     * 4. 若超时未通知(或者executeLocalTransaction返回 Unknown) → Broker 发起事务回查（check）
     *
     * 事务消息不能与延迟消息 / 批量消息一起使用
     * 事务消息只和 producer 有关, 对 consumer 透明
     */
    @SneakyThrows
    public static void main(String[] args) {
        TransactionMQProducer producer = new TransactionMQProducer("tx_producer_group");
        producer.setNamesrvAddr("localhost:9876");


        producer.setTransactionListener(new TransactionListener() {
            /**
             * COMMIT_MESSAGE	提交消息（消费者可见）
             * ROLLBACK_MESSAGE	回滚消息（删除，消费者不可见, 消息被丢弃）
             * UNKNOW 一段时间后, Broker 会回查, 确认 producer 的 db 事务是否执行完, 重走流程
             */
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object arg) {
                try {
                    // TODO: 执行本地事务，如数据库写入

                    // 如果成功：
                    return LocalTransactionState.COMMIT_MESSAGE;
                } catch (Exception e) {
                    // 如果失败：
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
            }

            /**
             *
             * 如果 Producer 在超时时间内 没有回应事务状态，Broker 会通过 checkLocalTransaction() 进行 状态回查。
             *
             * Broker 的回查频率和次数可以配置，例如：broker.conf
             * transactionCheckMax = 15
             * transactionCheckInterval = 60000  # 60秒
             *
             * 事务回查可能触发多次执行，需要保持幂等性
             */
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                // TODO: 查询本地事务状态（如查 DB）决定是否提交或回滚
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        });

        // 设置回查线程池, 事务消息一定要设置线程池，保证事务回查逻辑的可靠性和隔离性
        //在使用事务消息时，Broker 会在必要时 异步回查事务状态, 这个方法可能非常耗时, 需要专门放到线程池运行
        ExecutorService executor = new ThreadPoolExecutor(
            2,                          // corePoolSize
            5,                          // maximumPoolSize
            100,                        // keepAliveTime
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(2000),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("tx-check-thread");
                    return thread;
                }
            }
        );
        producer.setExecutorService(executor);

        Message msg = new Message("topic1", "hello tx".getBytes(StandardCharsets.UTF_8));
        TransactionSendResult sent = producer.sendMessageInTransaction(msg, null);

    }
}
