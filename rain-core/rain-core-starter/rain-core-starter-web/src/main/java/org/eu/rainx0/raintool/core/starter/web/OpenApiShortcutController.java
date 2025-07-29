package org.eu.rainx0.raintool.core.starter.web;

import org.apache.commons.lang3.StringUtils;
import org.eu.rainx0.raintool.core.starter.web.util.ServletTools;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * xiaoyureed@gmail.com
 * This controller is used to redirect the root path for every single application
 */
@Controller
@Slf4j
public class OpenApiShortcutController {

    private final Environment environment;

    public OpenApiShortcutController(Environment environment) {
        this.environment = environment;
    }

    /**
     * swagger document 根路径快速跳转
     */
    @GetMapping("/")
    public void index(HttpServletResponse response) throws Exception {
        String contextPath = environment.getProperty("server.servlet.context-path");
        ServletTools.redirect(response,
            (StringUtils.isNotEmpty(contextPath) ? contextPath : StringUtils.EMPTY).concat("/swagger-ui.html")
        );
        log.debug(";;swagger shortcut redirected");
    }
}