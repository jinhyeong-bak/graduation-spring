package com.example.demo.dto.account.request;


import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class RefreshRequest {
    private String email;
    private String refreshToken;
}
