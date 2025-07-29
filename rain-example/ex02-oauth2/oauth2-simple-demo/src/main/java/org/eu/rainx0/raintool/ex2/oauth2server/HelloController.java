package org.eu.rainx0.raintool.ex2.oauth2server;

import org.eu.rainx0.raintool.ex2.oauth2server.config.AuthServerConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaoyu
 * @time 2025/7/14 16:35
 */
@RestController
public class HelloController {
    @GetMapping("/hi")
    public ResponseEntity<String> hi() {
        return ResponseEntity.ok("hello world");
    }

    /**
     * 授权成功后，会携带 code, 跳转到这个端点
     * 可选, 若不存在这个端点/页面, 则会保持空白页, code 参数会在 url 中显示
     */
    @GetMapping("/authorized")
    public ResponseEntity<String> authorized(@RequestParam("code") String code) {
        return ResponseEntity.ok(code);
    }
}
