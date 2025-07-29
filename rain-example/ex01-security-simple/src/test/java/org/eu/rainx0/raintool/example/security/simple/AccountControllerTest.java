package org.eu.rainx0.raintool.example.security.simple;

import java.util.List;

import org.eu.rainx0.raintool.example.security.simple.controller.AccountController;
import org.eu.rainx0.raintool.example.security.simple.entity.Account;
import org.eu.rainx0.raintool.example.security.simple.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

/**
 * @author: xiaoyu
 * @time: 2025/7/9 23:03
 */
@WebMvcTest(AccountController.class)
public class AccountControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountRepository accountRepository;

    @Test
    void shouldReturnAccounts() throws Exception {
        given(accountRepository.findAll()).willReturn(List.of(new Account().setUsername("admin")));

        // mockMvc.perform(get("/accounts").with(user("xx")))
        //     .andExpect(status().isOk())
        //     .andExpect(jsonPath("$.data[0].username").value("admin"));


    }
}
