package org.eu.rainx0.raintool.ex3.springstream;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaoyu
 * @time 2025/7/20 10:43
 */
@RestController
public class OrderController {
    //在纯 REST 触发场景下手动发送消息，(即提供数据给某个 producer), 无需 Supplier
    @Autowired
    private StreamBridge streamBridge;

    /**
     * 场景：用户下单 → 风控校验 → 积分扣减 → 订单创建成功通知。
     */
    @PostMapping("/order")
    public String create(@RequestBody HashMap<String, Object> params) {
        // 发送到风控校验
        // 这里 toRisk-out-0 是作为producer, params 消息被发送到 order-event topic
        streamBridge.send("toRisk-out-0", params);
        return "已受理";
    }

    @Bean
    public ApplicationRunner runner() {
        return args -> {
            System.out.println("call order create");

            HashMap<String, Object> params = new HashMap<>();
            params.put("userId", "111");
            this.create(params);
        };
    }
}
