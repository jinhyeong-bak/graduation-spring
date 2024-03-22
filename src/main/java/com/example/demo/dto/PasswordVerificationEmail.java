package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PasswordVerificationEmail {
    private String verificationCode;
    private String verificationToken;
}
