// package org.eu.rainx0.raintool.core.starter.mq.kafka.config;
//
// import org.springframework.core.task.AsyncListenableTaskExecutor;
// import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
// import org.springframework.kafka.config.KafkaListenerEndpoint;
// import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
//
// /**
//  * @author xiaoyu
//  * @time 2025/7/24 17:26
//  */
// public class CustomConcurrentKafkaListenerContainerFactory<K,V> extends ConcurrentKafkaListenerContainerFactory<K,V> {
//
//
//     @Override
//     protected void initializeContainer(ConcurrentMessageListenerContainer<K, V> instance, KafkaListenerEndpoint endpoint) {
//         super.initializeContainer(instance, endpoint);
//         instance.getContainerProperties().setListenerTaskExecutor();
//     }
// }
