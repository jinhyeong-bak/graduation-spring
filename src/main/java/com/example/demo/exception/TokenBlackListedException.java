package com.example.demo.exception;

public class TokenBlackListedException extends RuntimeException{
    public TokenBlackListedException() {
        super();
    }

    public TokenBlackListedException(String message) {
        super(message);
    }

    public TokenBlackListedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenBlackListedException(Throwable cause) {
        super(cause);
    }
}
