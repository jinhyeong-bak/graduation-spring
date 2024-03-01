package com.example.demo.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
public class TokenResponse {
    private final String accessToken;
    private final String refreshToken;
}
