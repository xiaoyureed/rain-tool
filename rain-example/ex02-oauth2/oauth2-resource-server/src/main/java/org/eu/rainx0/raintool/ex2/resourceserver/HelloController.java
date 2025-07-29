package org.eu.rainx0.raintool.ex2.resourceserver;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaoyu
 * @time 2025/7/15 11:06
 */
@RestController
public class HelloController {
    @GetMapping("/hi")
    public String hi(Principal principal) {
        return "[protected] principal in resource server: " + principal.getName();
    }
}
