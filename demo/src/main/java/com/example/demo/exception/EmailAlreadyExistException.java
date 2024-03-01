package com.example.demo.exception;

import org.springframework.security.core.AuthenticationException;

public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException(String email) {
        super("Email already Exist: " + email);
    }

}
