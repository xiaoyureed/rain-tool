package org.eu.rainx0.raintool.ex.dubbo.simpledemo.provider;

import java.io.IOException;

import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.eu.rainx0.raintool.ex.dubbo.simpledemo.api.HelloService;

/**
 * @author xiaoyu
 * @time 2025/7/25 09:33
 */
public class DubboProvider {
    public static void main(String[] args) throws IOException {
        DubboBootstrap.getInstance()
            .application("hello-provider")
            .registry(b -> {
                // b.address("zookeeper://127.0.0.1:2181");
                b.address(RegistryConfig.NO_AVAILABLE);
            })
            .protocol(b -> {
                b.port(20880);
                b.name("dubbo");
            })
            .service(s -> {
                s.interfaceClass(HelloService.class);
                s.ref(new HelloServiceImpl());
            })
            .start()
            .await()
        ;
        // System.out.println("Dubbo Provider started.");
        //
        // System.out.println("Press any key to exit...");
        // System.in.read();
    }
}
