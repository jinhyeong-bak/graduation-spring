package com.example.demo.infrastructure.jwt;

import com.example.demo.dto.oauth.OAuthProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import java.util.Base64;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private final String key = JwtUtil.createKey();
    JwtUtil jwtUtil = new JwtUtil(key);


    @Test
    void getUserPk() {
        String token = jwtUtil.createToken(1L,  OAuthProvider.SELF, 1000L * 60);

        Long userPk = jwtUtil.getUserPk(token);

        assertThat(userPk).isEqualTo(1L);
    }


    @Test
    void validate_withValidToken_returnsTrue() {
        String token = jwtUtil.createToken(1L, OAuthProvider.SELF, 1000L * 60 * 60);

        boolean result = jwtUtil.validate(token);

        assertThat(result).isTrue();
    }

    @Test
    void validate_withDifferentKey_throwsSignatureException() {
        //given
        String token = jwtUtil.createToken(1L, OAuthProvider.SELF, 1000L * 60);

        //when
        String differentKey = JwtUtil.createKey();
        JwtUtil jwtUtilUsedDifferentKey = new JwtUtil(differentKey);

        //then
        assertThatThrownBy
                (() -> jwtUtilUsedDifferentKey.validate(token))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    void validate_withExpiredToken_throwsExpiredJwtException() {
        //given
        String token = jwtUtil.createToken(1L, OAuthProvider.SELF, 0);

        //when
        //then
        assertThatThrownBy(
                () -> jwtUtil.validate(token)
        ).isInstanceOf(ExpiredJwtException.class);
    }

}