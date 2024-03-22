package com.example.demo.exception;

import com.example.demo.dto.oauth.OAuthProvider;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class EmailAlreadyExistException extends RuntimeException {
    private OAuthProvider oAuthProvider;

    public EmailAlreadyExistException(String email, OAuthProvider oAuthProvider) {
        super("Email already Exist: " + email);
        this.oAuthProvider = oAuthProvider;
    }

}
