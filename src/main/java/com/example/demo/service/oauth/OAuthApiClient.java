package com.example.demo.service.oauth;

import com.example.demo.dto.oauth.OAuthInfoResponse;
import com.example.demo.dto.oauth.OAuthProvider;

public interface OAuthApiClient {
    OAuthProvider oAuthProvider();

    OAuthInfoResponse requestOauthInfo(String accessToken);
    String requestAccessToken(String authorizationCode);
}
