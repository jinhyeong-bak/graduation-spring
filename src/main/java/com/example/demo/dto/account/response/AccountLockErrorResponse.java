package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AccountLockErrorResponse extends ErrorResultResponse {
    public AccountLockErrorResponse(String error, String error_description, long lockTimeRemainingMillis) {
        super(error, error_description + ":" + lockTimeRemainingMillis);
    }

    public AccountLockErrorResponse(String error, long lockTimeRemainingMillis) {
        super(error, Long.toString(lockTimeRemainingMillis));
    }
}
