package org.eu.rainx0.raintool.example.security.simple;

import java.util.List;

import org.eu.rainx0.raintool.example.security.simple.entity.Account;
import org.eu.rainx0.raintool.example.security.simple.repository.AccountRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class SecuritySimpleApp {
    public static void main(String[] args) {
        SpringApplication.run(SecuritySimpleApp.class, args);

    }


    @Bean
    public ApplicationRunner init(PasswordEncoder passwordEncoder, AccountRepository accountRepository) {
        return args -> {
            accountRepository.saveAllAndFlush(List.of(
                // note: no "{bcrypt}" prefix in password
                new Account()
                    .setUsername("a2")
                    .setPassword(passwordEncoder.encode("a2"))
                    .setPhone("222")
                    .setEnabled(false),
                new Account()
                    .setUsername("a1")
                    .setPhone("111")
                    .setPassword(passwordEncoder.encode("a1"))
            ));
        };
    }
}
