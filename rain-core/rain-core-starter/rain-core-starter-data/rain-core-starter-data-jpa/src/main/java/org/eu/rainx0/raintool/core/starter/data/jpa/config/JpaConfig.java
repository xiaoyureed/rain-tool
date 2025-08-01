package org.eu.rainx0.raintool.core.starter.data.jpa.config;

import java.util.Optional;

import org.eu.rainx0.raintool.core.common.context.LoginHolder;
import org.eu.rainx0.raintool.core.common.model.LoginInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * @author: xiaoyu
 * @time: 2025/6/30 20:44
 */
@Configuration
// 开启审计, @CreatedBy 和 @LastModifiedBy 需要 AuditorAware支持
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfig {
    @Bean
    @ConditionalOnMissingBean
    public AuditorAware<String> auditorAware() {
        // 和 security 结合
        // return () -> {
        //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //     if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
        //         return Optional.empty();
        //     }
        //     var currentUser = (User) authentication.getPrincipal();
        //     return Optional.of(currentUser.getId());
        // };

        return () -> {

            try {
                LoginInfo loginInfo = LoginHolder.get(LoginInfo.class);
                return Optional.ofNullable(loginInfo).map(LoginInfo::getUsername);
            } catch (Exception e) {
                return Optional.empty();
            }
        };
    }

}
