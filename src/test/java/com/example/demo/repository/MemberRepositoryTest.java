package com.example.demo.repository;

import com.example.demo.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    final String email = "user@test.com";

    @Test
    void findByEmail_WithValidEmail_ReturnsMember() {
        //given
        Member savedMember = getSignUpMember(email);

        //when
        memberRepository.save(savedMember);
        Member foundMember = memberRepository.findByEmail(email).get();

        //then
        Assertions.assertThat(foundMember.getId()).isEqualTo(foundMember.getId());
        Assertions.assertThat(foundMember.getPassword()).isEqualTo(foundMember.getPassword());
        Assertions.assertThat(foundMember.getEmail()).isEqualTo(foundMember.getEmail());
    }

    private static Member getSignUpMember(String email) {
        return Member.createSignUpMember("user", email, "0000");
    }

    @Test
    void findByEmail_WithNonExistenceEmail_IsEmpty() {
        //when
        Optional<Member> foundMember = memberRepository.findByEmail(email);

        //then
        Assertions.assertThat(foundMember.isEmpty()).isEqualTo(true);
    }
}