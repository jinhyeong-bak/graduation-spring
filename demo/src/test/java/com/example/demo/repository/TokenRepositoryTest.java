package com.example.demo.repository;

import com.example.demo.infrastructure.jwt.JwtEntity;
import com.example.demo.infrastructure.jwt.JwtUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class TokenRepositoryTest {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    TokenRepository tokenRepository;

    @Test
    void findByUserPk_withValidPk_returnsToken() {
        // given
        String token = jwtUtil.createToken(1, 1000L * 60);
        JwtEntity savedJwtEntity = JwtEntity.createEntityWhenLogin(1L, token);

        //when
        tokenRepository.save(savedJwtEntity);
        JwtEntity foundJwtEntity = tokenRepository.findByUserPk(1L).get();

        //then
        assertThat(foundJwtEntity.getUserPk()).isEqualTo(savedJwtEntity.getUserPk());
        assertThat(foundJwtEntity.getRefreshToken()).isEqualTo(savedJwtEntity.getRefreshToken());
    }

    @Test
    void findByUserPk_withInvalidPk_returnsEmpty() {
        //when
        Optional<JwtEntity> byUserPk = tokenRepository.findByUserPk(1L);

        //then
        assertThat(byUserPk.isEmpty()).isEqualTo(true);
    }
}