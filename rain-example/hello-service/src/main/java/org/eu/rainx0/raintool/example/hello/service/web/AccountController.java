package org.eu.rainx0.raintool.example.hello.service.web;

import org.eu.rainx0.raintool.core.starter.webmybatis.AbstractBaseController;
import org.eu.rainx0.raintool.example.hello.service.AccountService;
import org.eu.rainx0.raintool.example.hello.service.entity.Account;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaoyu
 * @time 2025/7/28 11:14
 */
@RestController
@RequestMapping("/account")
public class AccountController extends AbstractBaseController<String, Account, AccountService> {
}
