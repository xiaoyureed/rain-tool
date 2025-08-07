package org.eu.rainx0.raintool.ex.mqtt.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author xiaoyu
 * @time 2025/8/5 15:11
 */
@RestController
@RequestMapping("/mqtt-callback")
public class MqttCallbackController {

    @PostMapping("/conn-status")
    public void onDeviceConnStatus(@RequestBody HashMap<String, Object> params) {
        System.out.println("onDeviceConnStatus: " + params);
        /*
        {conn_props={User-Property={}}, peername=192.168.65.1:54981,
         metadata={rule_id=device_connection_WH_D},
         clientid=ex07-mqtt-client-inbound, is_bridge=false, client_attrs={}, keepalive=60, proto_ver=4, proto_name=MQTT, connected_at=1754378711917, receive_maximum=32, sockname=172.18.0.2:1883, mountpoint=undefined, node=emqx@172.18.0.2, expiry_interval=0,
         event=client.connected,
         clean_start=true, username=undefined, timestamp=1754378711918}
        * */
    }
}
