package com.example.demo.dto.account.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResultResponse {
    private String error;
    private String error_description;
}
