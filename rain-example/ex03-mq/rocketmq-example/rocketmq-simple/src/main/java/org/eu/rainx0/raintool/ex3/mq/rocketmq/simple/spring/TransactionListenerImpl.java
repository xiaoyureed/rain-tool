package org.eu.rainx0.raintool.ex3.mq.rocketmq.simple.spring;

import java.nio.charset.StandardCharsets;

import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.apache.rocketmq.spring.support.RocketMQUtil;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.StringMessageConverter;

/**
 * @author xiaoyu
 * @time 2025/7/18 15:46
 */
@RocketMQTransactionListener(
    // 指定 producer bean name (1对 1 关系)
    rocketMQTemplateBeanName = "rocketMQTemplate"
)
public class TransactionListenerImpl
    // 不同与 rocketmq 内置的 TransactionListener 接口
    implements RocketMQLocalTransactionListener {
    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        Object transactionId = message.getHeaders().get(RocketMQHeaders.PREFIX + RocketMQHeaders.TRANSACTION_ID);

        org.apache.rocketmq.common.message.Message rocketMessage = RocketMQUtil.convertToRocketMessage(new StringMessageConverter(),
            StandardCharsets.UTF_8.name(), null, message
        );
        return null;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        return null;
    }
}
