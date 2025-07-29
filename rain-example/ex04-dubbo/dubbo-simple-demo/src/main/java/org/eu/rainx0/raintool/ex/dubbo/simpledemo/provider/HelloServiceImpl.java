package org.eu.rainx0.raintool.ex.dubbo.simpledemo.provider;

import org.eu.rainx0.raintool.ex.dubbo.simpledemo.api.HelloMsg;
import org.eu.rainx0.raintool.ex.dubbo.simpledemo.api.HelloService;

/**
 * @author xiaoyu
 * @time 2025/7/25 09:42
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public HelloMsg getMsg() {
        HelloMsg helloMsg = new HelloMsg();
        helloMsg.setMsg("hello");
        return helloMsg;
    }
}
