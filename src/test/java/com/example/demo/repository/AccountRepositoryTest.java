package com.example.demo.repository;

import com.example.demo.domain.Account;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@SpringBootTest
class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;
    final String email = "user@test.com";

    @Test
    void findByEmail_WithValidEmail_ReturnsMember() {
        //given
        Account savedAccount = getSignUpMember(email);

        //when
        accountRepository.save(savedAccount);
        Account foundAccount = accountRepository.findByEmail(email).get();

        //then
        Assertions.assertThat(foundAccount.getId()).isEqualTo(foundAccount.getId());
        Assertions.assertThat(foundAccount.getPassword()).isEqualTo(foundAccount.getPassword());
        Assertions.assertThat(foundAccount.getEmail()).isEqualTo(foundAccount.getEmail());
    }

    private static Account getSignUpMember(String email) {
        return Account.createSignUpMember("user", email, "0000");
    }

    @Test
    void findByEmail_WithNonExistenceEmail_IsEmpty() {
        //when
        Optional<Account> foundMember = accountRepository.findByEmail(email);

        //then
        Assertions.assertThat(foundMember.isEmpty()).isEqualTo(true);
    }
}