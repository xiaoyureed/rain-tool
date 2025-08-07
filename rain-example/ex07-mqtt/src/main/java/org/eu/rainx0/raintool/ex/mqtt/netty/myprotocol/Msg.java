package org.eu.rainx0.raintool.ex.mqtt.netty.myprotocol;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaoyu
 * @time 2025/8/7 20:35
 */
@Data
public abstract class Msg implements Serializable {
    public static Class<?> getMessageClass(int messageType) {
        return MESSAGE_CLASSES.get(messageType);
    }

    private int sequenceId;

    private int messageType;

    public abstract int getMessageType();

    static final int login_req = 0;
    static final int login_resp = 1;

    private static final Map<Integer, Class<?>> MESSAGE_CLASSES = new HashMap<>();

    static {
        MESSAGE_CLASSES.put(login_req, LoginReq.class);
        MESSAGE_CLASSES.put(login_resp, LoginResp.class);
    }
}
