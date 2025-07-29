package org.eu.rainx0.raintool.core.starter.mq.kafka.config;

import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import lombok.extern.slf4j.Slf4j;

/**
 * 自定义消息分发到 partition 的规则
 * 需要配置 spring.kafka.producer.properties.partitioner.class: 本类名
 *
 * 默认规则是:
 * ① 若发送消息时指定了分区（即自定义分区策略），则直接将消息append到指定分区；
 * ② 若发送消息时未指定 patition，但指定了 key（kafka允许为每条消息设置一个key），则对key值进行hash计算，根据计算结果路由到指定分区，这种情况下可以保证同一个 Key 的所有消息都进入到相同的分区；
 * ③  patition 和 key 都未指定，则使用kafka默认的分区策略，轮询选出一个 patition；
 */
@Slf4j
public class PartitionSelector implements Partitioner {
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        // 自定义分区规则(这里是全部发到0号分区)
        log.info("自定义分区策略 topic:{} key:{} value:{}", topic, key, value.toString());
        return 0;
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}