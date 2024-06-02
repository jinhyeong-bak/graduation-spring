package com.example.demo.controller.account;

import com.example.demo.dto.account.response.TokenPair;
import com.example.demo.dto.oauth.KakaoTokens;
import com.example.demo.dto.oauth.OAuthProvider;
import com.example.demo.service.oauth.KakaoApiClient;
import com.example.demo.service.oauth.OAuthAccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account/oauth")
@AllArgsConstructor
public class OAuthAccountController {

    private OAuthAccountService oAuthAccountService;
    private KakaoApiClient kakaoApiClient;

    @GetMapping("/kakao/getAccessToken")
    public String getAccessToken(@RequestParam("code") String authorizationcode) {
        return kakaoApiClient.requestAccessToken(authorizationcode);
    }
    @PostMapping("/kakao/login")
    public ResponseEntity<TokenPair> login(@RequestBody KakaoTokens kakaoTokens) {
        return ResponseEntity.ok().body(oAuthAccountService.login(kakaoTokens.getAccessToken(), OAuthProvider.KAKAO));
    }
}
