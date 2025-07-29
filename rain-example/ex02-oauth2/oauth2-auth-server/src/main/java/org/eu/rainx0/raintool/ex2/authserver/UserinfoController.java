package org.eu.rainx0.raintool.ex2.authserver;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaoyu
 * @time 2025/7/16 14:33
 */
@RestController
public class UserinfoController {

    /**
     * client app 进行 oauth2 认证时, 需要获得认证用户的基本信息
     */
    @GetMapping("/userinfo")
    public Map<String, Object> userInfo(
        @AuthenticationPrincipal Jwt jwt
    ) {
        HashMap<String, Object> ret = new HashMap<>();

        System.out.println(jwt.getClaims());

        ret.put("sub", jwt.getSubject());
        ret.put("name", jwt.getClaimAsString("name"));

        return ret;
    }
}
