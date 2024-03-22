package com.example.demo.service.oauth;

import com.example.demo.domain.OAuthAccount;
import com.example.demo.dto.TokenResponse;
import com.example.demo.dto.oauth.OAuthInfoResponse;
import com.example.demo.dto.oauth.OAuthProvider;
import com.example.demo.exception.EmailAlreadyExistException;
import com.example.demo.infrastructure.jwt.JwtEntity;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.repository.OAuthAccountRepository;
import com.example.demo.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OAuthAccountService {
    private final AccountService accountService;
    private final OAuthAccountRepository oAuthAccountRepository;
    private final JwtUtil jwtUtil;
    private final TokenRepository tokenRepository;
    private final RequestOAuthInfoService requestOAuthInfoService;

    @Value("${security.jwt.validTime.accessToken}")
    private long accessValidTime;           // 제한시간 30분
    @Value("${security.jwt.validTime.refreshToken}")
    private long refreshValidTime;          // 제한 시간 일년

    public TokenResponse login(String oAuthAccessToken, OAuthProvider oAuthProviderInRequest) {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.requestAccountInfo(oAuthProviderInRequest, oAuthAccessToken);

        String userEmail = oAuthInfoResponse.getEmail();

        // 존재하는 회원인지 검사
        Optional<OAuthAccount> optionalAccount = oAuthAccountRepository.findByEmail(userEmail);

        // 서버에 존재하는 이메일이고 provider 일치하면 토큰 반환
        if(optionalAccount.isPresent()) {
            OAuthAccount oAuthAccount = optionalAccount.get();

            if(oAuthProviderInRequest == oAuthAccount.getOAuthProvider()) {
                return getTokenResponse(oAuthProviderInRequest, oAuthAccount);
            }
        }

        // 자체 로그인 또는 다른 provider로 저장 돼 있는 이메일
        if(accountService.isEmailExists(userEmail) || optionalAccount.isPresent()) {
            OAuthProvider oAuthProvider = optionalAccount.isPresent() ? oAuthProviderInRequest : OAuthProvider.SELF;

            log.error("다른 OAuthProvider로 저장된 이메일이 회원 가입 요청으로 넘어왔다. email: {} OAuthProvider: {}", userEmail, oAuthProviderInRequest);
            throw new EmailAlreadyExistException(userEmail, oAuthProvider);
        }

        // 존재하지 않으므로 회원 가입
        OAuthAccount newAccount = OAuthAccount.builder()
                .email(userEmail)
                .oAuthProvider(oAuthProviderInRequest)
                .build();
        oAuthAccountRepository.save(newAccount);

        // 토큰 반환
        return getTokenResponse(oAuthProviderInRequest, newAccount);
    }

    private TokenResponse getTokenResponse(OAuthProvider oAuthProviderInRequest, OAuthAccount oAuthAccount) {
        String accessToken = jwtUtil.createToken(oAuthAccount.getId(), oAuthProviderInRequest, accessValidTime);
        String refreshToken = jwtUtil.createToken(oAuthAccount.getId(), oAuthProviderInRequest, refreshValidTime);

        JwtEntity jwtEntity = JwtEntity.createEntityWhenLogin(oAuthAccount.getId(), oAuthProviderInRequest, refreshToken);
        tokenRepository.save(jwtEntity);

        return new TokenResponse(accessToken, refreshToken);
    }


}
