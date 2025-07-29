package org.eu.rainx0.raintool.core.starter.util;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.StringValueResolver;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: xiaoyu
 * @time: 2025/6/29 17:35
 */
@Slf4j
@ComponentScan // This annotation here is used to scan the other beans
public class Scanner implements InitializingBean, EmbeddedValueResolverAware {

    {
        log.debug(";;Util starter loaded.");
    }

    @Autowired
    private ApplicationContext applicationContext;
    private StringValueResolver stringValueResolver;

    @Override
    public void afterPropertiesSet() throws Exception {
        SpringContextTools.context = applicationContext;
        SpringContextTools.stringValueResolver = stringValueResolver;
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.stringValueResolver = resolver;
    }
}
