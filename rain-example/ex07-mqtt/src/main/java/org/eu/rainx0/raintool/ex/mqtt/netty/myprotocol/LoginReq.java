package org.eu.rainx0.raintool.ex.mqtt.netty.myprotocol;

import lombok.Data;

/**
 * @author xiaoyu
 * @time 2025/8/7 20:42
 */
@Data
public class LoginReq extends Msg{
    private String username;
    private String password;

    @Override
    public int getMessageType() {
        return login_req;
    }
}
