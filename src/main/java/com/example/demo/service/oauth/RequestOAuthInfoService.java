package com.example.demo.service.oauth;

import com.example.demo.dto.oauth.OAuthInfoResponse;
import com.example.demo.dto.oauth.OAuthProvider;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RequestOAuthInfoService {
    private final Map<OAuthProvider, OAuthApiClient> clients;

    public RequestOAuthInfoService(List<OAuthApiClient> clients) {
        this.clients = clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthApiClient::oAuthProvider, Function.identity())
        );
    }

    public OAuthInfoResponse requestAccountInfo(OAuthProvider provider, String accessToken) {
        OAuthApiClient client = clients.get(provider);
        return client.requestOauthInfo(accessToken);
    }
}
