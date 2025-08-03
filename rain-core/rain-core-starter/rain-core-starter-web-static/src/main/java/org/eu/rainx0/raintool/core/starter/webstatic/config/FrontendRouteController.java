package org.eu.rainx0.raintool.core.starter.webstatic.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author xiaoyu
 * @time 2025/8/3 11:41
 */
@Controller
@Conditional(FrontendRouteController.FrontendControllerrCondition.class)
public class FrontendRouteController {

    /**
     * ^ 出现在方括号中 [^...] → 表示“非”, 这里表示匹配不是 '.'的字符
     * 所以它不会匹配像：
     * /main.js
     * /style.css
     * 它只会匹配：
     * /home
     * /login
     * /users
     */
    @RequestMapping(value = "/{path:[^\\.]*}")
    public String redirect() {
        return "forward:/index.html";
    }

    static class FrontendControllerrCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            WebStaticProps props = context.getBeanFactory().getBean(WebStaticProps.class);
            // webstatic 开启并且 有后端逻辑, 则启用这个 controller
            return props.isEnabled() && !props.isNoBackendLogic();
        }
    }
}
