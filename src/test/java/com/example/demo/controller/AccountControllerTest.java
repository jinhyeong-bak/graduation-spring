package com.example.demo.controller;

import com.example.demo.dto.TokenResponse;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.service.AccountService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import({TokenFilterTest.FilterTestConfiguration.class, JwtUtil.class})
class AccountControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    JwtUtil jwtUtil;
    @Mock
    AccountService accountService;
    @Autowired
    MockMvc mockMvc;

    @TestConfiguration
    static class AccountControllerTestConfig {
        @Bean
        @Primary
        AccountService accountService() {
            return Mockito.mock(AccountService.class);
        }
    }

    @BeforeEach
    public void setup() {
        this.accountService = (AccountService)this.wac.getBean(AccountService.class);
    }

    @Test
    void refresh() throws Exception {
        String email = "user@test.com";
        String refreshToken = jwtUtil.createToken(0L, 1000L * 60 * 60);
        String refreshedAccessToken = jwtUtil.createToken(0L, 1000L * 60 * 60);

        when(accountService.refresh(email, refreshToken)).thenReturn(new TokenResponse(refreshedAccessToken, refreshToken));

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/account/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\"," + " \"refreshToken\":\"" + refreshToken + "\"}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value(refreshedAccessToken))
                .andExpect(jsonPath("$.refreshToken").value(refreshToken));
    }
}