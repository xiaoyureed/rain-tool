package org.eu.rainx0.raintool.ex.dubbo.simpledemo.consumer;

import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.eu.rainx0.raintool.ex.dubbo.simpledemo.api.HelloService;

/**
 * @author xiaoyu
 * @time 2025/7/25 10:05
 */
public class DubboConsumer {
    public static void main(String[] args) {

        ReferenceConfig<HelloService> helloServiceReferenceConfig = new ReferenceConfig<>();
        helloServiceReferenceConfig.setInterface(HelloService.class);
        helloServiceReferenceConfig.setUrl("dubbo://127.0.0.1:20880");
        // helloServiceReferenceConfig.setUrl("dubbo://127.0.0.1:20880/org.eu.rainx0.raintool.ex.dubbo.simpledemo.api.HelloService");

        DubboBootstrap.getInstance()
            .application("hello-consumer")
            .registry(b -> {
                b.address(RegistryConfig.NO_AVAILABLE);
            })
            .reference(helloServiceReferenceConfig)
            .start();

        HelloService helloService = helloServiceReferenceConfig.get();
        System.out.println(helloService.getMsg());
    }
}
