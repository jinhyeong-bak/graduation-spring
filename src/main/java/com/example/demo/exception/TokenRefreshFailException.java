package com.example.demo.exception;

import io.jsonwebtoken.JwtException;
import org.springframework.security.core.AuthenticationException;

public class TokenRefreshFailException extends JwtException {
    public TokenRefreshFailException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public TokenRefreshFailException(String msg) {
        super(msg);
    }
}
