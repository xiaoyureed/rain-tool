package org.eu.rainx0.raintool.core.starter.mq.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.IsolationLevel;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eu.rainx0.raintool.core.starter.mq.kafka.listener.KafkaProducerLoggingListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerAwareListenerErrorHandler;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 16:43
 */
@Configuration
public class KafkaConfig {

    /**
     * 生产者配置信息
     */
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();

        /**
         * Kafka 会自动启用以下配置，并忽略你手动设置的这些配置值
         * acks     "all"   所有副本都确认后才认为消息发送成功
         * retries
         *      Integer.MAX_VALUE   无限重试，直到成功
         * max.in.flight.requests.per.connection
         *      5  最多允许 5 个未确认的请求（保证顺序
         */

        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);

        /**
         * "0"：生产者不等待任何确认，消息发送后即认为成功。性能最高，但可能丢失消息。
         * "1"：只需要 leader 副本确认。性能和安全性平衡。
         * "-1" 或 "all"：需要所有同步副本都确认。最安全，但性能最低。
         */
        props.put(ProducerConfig.ACKS_CONFIG, "0");
        // 生产者发送失败时的重试次数。(0为不启用重试机制，幂等性的时候必须大于0)
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // 压缩消息，支持四种类型，分别为：none、lz4、gzip、snappy，默认为none。
        // 消费者默认支持解压，所以压缩设置在生产者，消费者无需设置。
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,"none");

        /**
         * 设置生产者批量发送消息的批次大小（以字节为单位）。
         * 当多个消息发送到同一分区时，生产者会将它们打包成批次发送，以提高吞吐量。
         * 16384 (16KB) 字节
         */
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
        /**
         * 设置生产者在发送批次之前等待更多消息加入批次的时间（毫秒）。
         * 即使批次未满，也会在 linger.ms 时间到达后发送, 启用该功能能有效减少生产者发送消息次数，从而提高并发量
         */
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        /**
         * 设置生产者可用于缓冲等待发送到服务器的消息的总内存字节数。
         * 当生产者发送消息的速度超过发送到服务器的速度时，生产者会将消息缓冲在内存中。
         * 33554432 (32MB)
         */
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
        // 消息的最大大小限制,也就是说send的消息大小不能超过这个限制, 默认1048576(1MB)
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG,1048576);

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // 手动控制序列化更好
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        /**
         * transaction-id-prefix: 事务编号前缀
         *
         * isolation-level: read_committed :仅读取已提交的消息
         */
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, null);

        return props;
    }

    /**
     * 生产者工厂
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Autowired(required = false)
    private KafkaProducerLoggingListener<String, Object> loggingListener;

    /**
     * 生产者模板
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        KafkaTemplate<String, Object> template = new KafkaTemplate<>(producerFactory());
        template.setProducerListener(loggingListener);
        return template;
        // or
        // return new KafkaTemplate<>(producerFactory(), props);
    }

    /**
     * 自动确认消费者配置信息
     */
    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();

        /**
         * 消费者组是 Kafka 实现消息广播/负载均衡的核心机制
         */
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "default-consumer-group");

        // offset偏移量规则设置：
        // (1)、earliest：当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，从头开始消费
        // (2)、latest：当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，消费新产生的该分区下的数据
        // (3)、none：topic各分区都存在已提交的offset时，从offset后开始消费；只要有一个分区不存在已提交的offset，则抛出异常
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // 关闭自动提交
        // 自动提交的频率(ms)
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");

        /**
         * 设置消费者一次拉取的消息数量
         */
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);

        /**
         * 会话超时时间, 120s
         */
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 120 * 1000);
        /**
         * 请求超时时间(180s)
         */
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 180 * 1000);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        /**
         * # 事务隔离级别, 这里是 仅读取已提交的消息
         */
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED);


        return props;
    }

    /**
     * 简单消费者工厂
     * 在消费者 listener 中配置 containerFactory = "simpleFactory"
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> simpleFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        // 设置消费者工厂
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumerConfigs()));

        // 消费者组中线程数量
        factory.setConcurrency(3);

        // 拉取超时时间
        factory.getContainerProperties().setPollTimeout(3000);

        // 设置确认模式
        // RECORD  每处理一条commit一次
        // BATCH(默认) 每次poll的时候批量提交一次，频率取决于每次poll的调用频率
        // TIME  每次间隔ackTime的时间去commit
        // COUNT 累积达到ackCount次的ack去commit
        // COUNT_TIME ackTime或ackCount哪个条件先满足，就commit
        // MANUAL listener负责ack，但是背后也是批量上去
        // MANUAL_IMMEDIATE listner负责ack，每调用一次，就立即commit
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);


        factory.setBatchListener(false);// 关闭批量消费（默认就是关闭）

        // 被过滤的消息将被丢弃
        factory.setAckDiscarded(true);
        // 消息过滤策略
        factory.setRecordFilterStrategy(consumerRecord -> {
            if (Integer.parseInt(consumerRecord.value().toString()) % 2 == 0) {
                return false;
            }
            // 返回true消息则被过滤
            return true;
        });

        // 设置发送消息的 template
        // 这是为了支持 @SendTo注解, 以支持消费后立即转发
        // (在消费者Listener上添加SendTo注解，可以将消息转发到SendTo指定的Topic中, 方法return 值就是要转发的消息)
        factory.setReplyTemplate(kafkaTemplate());
        factory.setCommonErrorHandler(kafkaErrorHandler());

        //禁止自动启动
        // factory.setAutoStartup(false);

        return factory;
    }

    /**
     * 捕获 listener error
     *
     * 需要在 在listener上配置errorHandler属性 (errorHandler = "consumerAwareErrorHandler")
     */
    @Bean
    public ConsumerAwareListenerErrorHandler consumerAwareListenerErrorHandler() {
        return new ConsumerAwareListenerErrorHandler() {
            @Override
            public Object handleError(Message<?> message, ListenerExecutionFailedException e, Consumer<?, ?> consumer) {
                System.out.println("consumer error: " + message.getPayload());
                return "exception";
            }
        };
    }


    @Bean
    public CommonErrorHandler kafkaErrorHandler() {
        ConsumerRecordRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate());
        // 间隔3秒，重试2次，共三次
        BackOff backOff = new FixedBackOff(3000, 2);
        return new DefaultErrorHandler(recoverer, backOff);
    }

    //////// 定时启停消费 //////////

    @Configuration
    @EnableScheduling
    public static class SchedulingListenerConfig {

        @Autowired
        private KafkaListenerEndpointRegistry registry;

        @Autowired
        private ConsumerFactory consumerFactory;

        // 监听器容器工厂(设置禁止KafkaListener自启动)
        @Bean
        public ConcurrentKafkaListenerContainerFactory delayContainerFactory() {
            ConcurrentKafkaListenerContainerFactory container = new ConcurrentKafkaListenerContainerFactory();
            container.setConsumerFactory(consumerFactory);
            // 禁止KafkaListener自启动
            container.setAutoStartup(false);
            return container;
        }

        // 监听器
        @KafkaListener(id = "timingConsumer", topics = "topic1", containerFactory = "delayContainerFactory")
        public void onMessage1(ConsumerRecord<?, ?> record) {
            System.out.println("消费成功：" + record.topic() + "-" + record.partition() + "-" + record.value());
        }

        // 定时启动监听器
        @Scheduled(cron = "0 42 11 * * ? ")
        public void startListener() {
            System.out.println("启动监听器...");
            // "timingConsumer"是@KafkaListener注解后面设置的监听器ID,标识这个监听器
            if (!registry.getListenerContainer("timingConsumer").isRunning()) {
                registry.getListenerContainer("timingConsumer").start();
            }
            // registry.getListenerContainer("timingConsumer").resume();
        }

        // 定时停止监听器
        @Scheduled(cron = "0 45 11 * * ? ")
        public void shutDownListener() {
            System.out.println("关闭监听器...");
            registry.getListenerContainer("timingConsumer").pause();
        }
    }

    //////// 定时启停消费 //////////
}