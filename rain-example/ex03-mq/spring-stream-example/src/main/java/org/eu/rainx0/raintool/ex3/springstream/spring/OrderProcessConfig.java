package org.eu.rainx0.raintool.ex3.springstream.spring;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaoyu
 * @time 2025/7/20 10:16
 */
@Configuration
public class OrderProcessConfig {
    /**
     * 风控检测
     * 函数命名约定：{beanName}-in-0 / {beanName}-out-0 与配置中的 binding 名称保持一致
     * riskCheck 这个 bean  name + "in-0"/"out-0" 将作为 binding name
     * (即:
     *    riskCheck-in-0 作为consumer, 消费 order-event  topic 中的消息, 执行校验
     *    riskCheck-out-0 作为producer , 将校验后的 Order 发送到 risk-passed topic 中
     * )
     */
    @Bean
    public Function<HashMap<String, Object>, HashMap<String, Object>> riskCheck() {
        return order -> {

            System.out.println("风控校验开始");

            String userId = order.get("userId").toString();
            if (userId.startsWith("0")) {
                order.put("risk", true);
            }
            return order;
        };
    }

    /**
     * 扣减积分
     */
    @Bean
    public Function<HashMap<String, Object>, HashMap<String, Object>> deductScore() {
        return order -> {

            System.out.println("积分扣减开始");

            order.put("deducted", true);
            return order;
        };
    }

    /**
     * 通知
     */
    @Bean
    public Consumer<HashMap<String, Object>> notifyUser() {
        return order -> {

            System.out.println("通知用户订单完成");

        };
    }
}
