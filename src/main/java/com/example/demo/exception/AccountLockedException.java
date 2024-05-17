package com.example.demo.exception;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AccountLockedException extends RuntimeException{
    private long lockTimeRemainingMillis;
    public AccountLockedException(String message, String lockTimeRemainingMillis) {
        super(message + ":" + lockTimeRemainingMillis);
    }

    public AccountLockedException(String message, long lockTimeRemainingMillis) {
        super(message);
        this.lockTimeRemainingMillis = lockTimeRemainingMillis;
    }

}
