package org.eu.rainx0.raintool.example.security.simple;

import java.util.List;

import org.eu.rainx0.raintool.example.security.simple.entity.Account;
import org.eu.rainx0.raintool.example.security.simple.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.*;

/**
 * @author: xiaoyu
 * @time: 2025/7/9 23:44
 */
@DataJpaTest
public class AccountRepositoryTest {
    @Autowired
    AccountRepository accountRepository;

    @Test
    void testAdd() {
        accountRepository.save(new Account().setUsername("hello").setPassword("hello"));
        List<Account> all = accountRepository.findAll();
        assertThat(all.size()).isEqualTo(1);
    }
}
