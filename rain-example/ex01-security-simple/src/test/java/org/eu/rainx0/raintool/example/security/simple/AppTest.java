package org.eu.rainx0.raintool.example.security.simple;

import java.util.List;

import org.eu.rainx0.raintool.example.security.simple.entity.Account;
import org.eu.rainx0.raintool.example.security.simple.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author: xiaoyu
 * @time: 2025/7/9 22:49
 */
// @SpringBootTest(classes = {xxxConfig.class})
@SpringBootTest
// @ActiveProfiles("test")
// @TestPropertySource(locations = "classpath:application-test.yml")
// @Sql(scripts = "classpath:db/test-data.sql")
// @AutoConfigureMockMvc // 测试 web 层
public class AppTest {
    @Autowired
    AccountRepository accountRepository;

    @Test
    void testAddAccount() {
        accountRepository.saveAndFlush(new Account().setUsername("a1").setPassword("a1"));
        List<Account> all = accountRepository.findAll();
        System.out.println( all);
    }
}
