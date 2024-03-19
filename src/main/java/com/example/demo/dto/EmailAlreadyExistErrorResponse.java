package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.example.demo.dto.oauth.OAuthProvider;
import lombok.Getter;

@Getter
public class EmailAlreadyExistErrorResponse extends ErrorResultResponse{

    private OAuthProvider oAuthProvider;
    public EmailAlreadyExistErrorResponse(String error, String error_description, OAuthProvider oAuthProvider) {
        super(error, error_description);
        this.oAuthProvider = oAuthProvider;
    }
}
