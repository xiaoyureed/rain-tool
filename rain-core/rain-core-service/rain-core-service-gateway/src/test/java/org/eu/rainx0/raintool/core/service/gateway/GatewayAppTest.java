package org.eu.rainx0.raintool.core.service.gateway;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;

/**
 * @author xiaoyu
 * @time 2025/7/28 21:21
 */
@SpringBootTest
public class GatewayAppTest {

    @Autowired
    DiscoveryClient discoveryClient;

    /**
     * @LoadBalanced 修饰 RestTemplate @bean , 会自动使用负载均衡 (即 url 中可以用 service name 代替 host:port)
     */
    @Autowired
    LoadBalancerClient loadBalancerClient;

    /**
     * auto refresh:
     * - @value + @RefreshScope
     * - @ConfigurationProperties (无需 @refreshScope)
     */

    @Test
    void test1() {
        List<String> serviceNames = discoveryClient.getServices();
        serviceNames.forEach(System.out::println); // rain-core-service-gateway

        List<ServiceInstance> instances = discoveryClient.getInstances("rain-core-service-gateway");
        for (ServiceInstance ins : instances) {
            String host = ins.getHost();
            int port = ins.getPort();
            URI uri = ins.getUri(); // http://192.168.31.109:8888
            Map<String, String> metadata = ins.getMetadata();
            // {nacos.instanceId=192.168.31.109#8888#DEFAULT#DEFAULT_GROUP@@rain-core-service-gateway,
            // nacos.weight=1.0,
            // nacos.cluster=DEFAULT,
            // nacos.ephemeral=true,
            // nacos.healthy=true,
            // preserved.register.source=SPRING_CLOUD}

            System.out.println(host + ":" + port + ":" + uri + ":" + metadata);
        }

        ServiceInstance ins = loadBalancerClient.choose("rain-core-service-gateway"); // 负载均衡, 随机选择一个实例
    }
}
