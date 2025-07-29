package org.eu.rainx0.raintool.core.starter.nacos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author: xiaoyu
 * @time: 2025/7/1 11:36
 */
@Component
public class NacosService {
    @Autowired
    private NacosServiceManager nacosServiceManager;
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    public InstanceProperties getInstanceProperties(String instanceName) {
        // deprecated
//        NamingService namingService = nacosServiceManager.getNamingService(nacosDiscoveryProperties.getNacosProperties());
        NamingService namingService = nacosServiceManager.getNamingService();
//        NamingService namingService = null;
//        try {
//            namingService = NacosFactory.createNamingService(nacosDiscoveryProperties.getNacosProperties());
//        } catch (NacosException e) {
//            //todo
//            throw new RuntimeException(e);
//        }

        Instance instance = null;
        try {
            instance = namingService.selectOneHealthyInstance(instanceName, nacosDiscoveryProperties.getGroup());
        } catch (NacosException e) {
            //todo
            e.printStackTrace();
        }

        return new InstanceProperties().setPort(instance.getPort()).setIp(instance.getIp());
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    public static class InstanceProperties {
        private String ip;
        private Integer port;
    }
}
