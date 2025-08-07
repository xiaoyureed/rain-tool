package org.eu.rainx0.raintool.ex.mqtt.netty.myprotocol;

/**
 * @author xiaoyu
 * @time 2025/8/7 20:42
 */
public class LoginResp extends Msg{
    private boolean success;
    private String reason;

    @Override
    public int getMessageType() {
        return login_resp;
    }
}
