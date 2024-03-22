package com.example.demo.service.oauth;

import com.example.demo.dto.oauth.KakaoInfoResponse;
import com.example.demo.dto.oauth.KakaoTokens;
import com.example.demo.dto.oauth.OAuthInfoResponse;
import com.example.demo.dto.oauth.OAuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoApiClient implements OAuthApiClient{
    private static final String GRANT_TYPE = "authorization_code";

    @Value("${oauth.kakao.url.auth}")
    private String authUrl;
    @Value("${oauth.kakao.url.api}")
    private String apiUrl;
    @Value("${oauth.kakao.client_id}")
    private String clientId;

    private final RestTemplate restTemplate;

    @Override
    public OAuthProvider oAuthProvider() {
        return OAuthProvider.KAKAO;
    }

    @Override
    public OAuthInfoResponse requestOauthInfo(String accessToken) {
        String url = apiUrl + "/v2/user/me";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        log.info("accessToken: {}", accessToken);
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("property_keys", "[\"kakao_account.email\"]");

        HttpEntity<?> request = new HttpEntity<>(httpHeaders);

        return restTemplate.postForObject(url, request, KakaoInfoResponse.class);
    }

    @Override
    public String requestAccessToken(String authorizationCode) {
        String url = authUrl + "/oauth/token";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        body.add("grant_type", GRANT_TYPE);
        body.add("client_id", clientId);

        HttpEntity<?> request = new HttpEntity<>(body, httpHeaders);

        KakaoTokens response = restTemplate.postForObject(url, request, KakaoTokens.class);

        assert response != null;
        return response.getAccessToken();
    }

}
