package com.example.demo.controller;

import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.security.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
@Import({JwtUtil.class, JwtAuthenticationFilter.class})
class TokenFilterTest {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    MockMvc mockMvc;

    @TestConfiguration
    static class FilterTestConfiguration {

        @Autowired
        JwtAuthenticationFilter jwtAuthenticationFilter;

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

            http = http .cors(cors -> cors.disable())
                    .csrf(csrf -> csrf.disable());

            http = http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

            http = http.addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
        }
    }


    @Test
    void tokenFilterTest_withValidAccessToken_returnsHttpStatus200() throws Exception {
        String accessToken = jwtUtil.createToken(1L, 60 * 1000L);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/auth/tokenFilterTest")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                )
                .andExpect(status().isOk());
    }

    @Test
    void tokenFilterTest_withInvalidAccessToken_returnsSignatureException() throws Exception {
        JwtUtil jwtUtilUsedDifferentKey = new JwtUtil(JwtUtil.createKey());
        String accessToken = jwtUtilUsedDifferentKey.createToken(0L, 1000L * 60);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/auth/tokenFilterTest")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenFilterTest_withExpiredAccessToken_returnsTokenRefreshFailException() throws Exception {
        String accessToken = jwtUtil.createToken(0L, 0);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/auth/tokenFilterTest")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void tokenFilterTest_withBlacklistedToken_returnsTokenBlacklistedException() throws Exception {
        String accessToken = jwtUtil.createToken(0L, 0);

        mockMvc.perform(
                Mock
        )
    }
 }