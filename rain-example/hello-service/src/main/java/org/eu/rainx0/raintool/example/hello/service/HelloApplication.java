package org.eu.rainx0.raintool.example.hello.service;

import java.util.ArrayList;
import java.util.List;

import org.eu.rainx0.raintool.example.hello.service.entity.Account;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.CollectionUtils;

/**
 * @author: xiaoyu
 * @time: 2025/7/2 13:26
 */
@SpringBootApplication
public class HelloApplication {
    public static void main(String[] args) {
        SpringApplication.run(HelloApplication.class, args);
    }

    @Bean
    public ApplicationRunner applicationRunner(AccountMapper mapper) {
        return args -> {
            Account a1 = new Account().setUsername("a").setPassword("a").setEnabled(false);
            Account a2 = new Account().setUsername("b").setPassword("b"); // enabled default to 1, which means true
            List<Account> accounts = new ArrayList<>();
            accounts.add(a1);
            accounts.add(a2);

            mapper.insertBatch(accounts);
        };
    }
}
