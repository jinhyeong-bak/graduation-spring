package com.example.demo.dto.account.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PasswordVerificationEmail {
    private String verificationCode;
    private String verificationToken;
}
