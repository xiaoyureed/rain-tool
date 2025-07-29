package org.eu.rainx0.raintool.example.security.simple.repository;

import java.util.Optional;

import org.eu.rainx0.raintool.core.starter.data.jpa.x.IBaseRepository;
import org.eu.rainx0.raintool.example.security.simple.entity.Account;
import org.springframework.stereotype.Repository;

/**
 * @author: xiaoyu
 * @time: 2025/7/9 15:22
 */
@Repository
public interface AccountRepository extends IBaseRepository<Account, String> {
    Optional<Account> findAccountByUsername(String username);
    Optional<Account> findAccountByPhone(String phone);
}
