package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AccountLockErrorResponse extends ErrorResultResponse {
    private long lockTimeRemainingMillis;
    public AccountLockErrorResponse(String error, String error_description, long lockTimeRemainingMillis) {
        super(error, error_description);
        this.lockTimeRemainingMillis = lockTimeRemainingMillis;
    }
}
