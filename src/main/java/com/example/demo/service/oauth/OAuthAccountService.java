package com.example.demo.service.oauth;

import com.example.demo.domain.Account;
import com.example.demo.dto.TokenPair;
import com.example.demo.dto.oauth.OAuthInfoResponse;
import com.example.demo.dto.oauth.OAuthProvider;
import com.example.demo.exception.EmailAlreadyExistException;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.repository.AccountRepository;
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
    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;
    private final RequestOAuthInfoService requestOAuthInfoService;

    @Value("${security.jwt.validTime.accessToken}")
    private long accessValidTime;           // 제한시간 30분
    @Value("${security.jwt.validTime.refreshToken}")
    private long refreshValidTime;          // 제한 시간 일년

    public TokenPair login(String oAuthAccessToken, OAuthProvider oAuthProviderInRequest) {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.requestAccountInfo(oAuthProviderInRequest, oAuthAccessToken);

        String userEmail = oAuthInfoResponse.getEmail();

        // 존재하는 회원인지 검사
        Optional<Account> optionalAccount = accountRepository.findByEmail(userEmail);

        // 서버에 존재하는 이메일이고 provider 일치하면 토큰 반환
        if(optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            OAuthProvider oAuthProviderInAccount = account.getOAuthProvider();

            //provider 일치
            if(oAuthProviderInRequest == oAuthProviderInAccount) {
                return getTokenPair(oAuthProviderInRequest, account);
            }

            // 자체 로그인 또는 다른 provider로 저장 돼 있는 이메일
            else {
                log.error("다른 OAuthProvider로 저장된 이메일이 회원 가입 요청으로 넘어왔다. email: {} OAuthProviderInRequest: {}, OAuthProviderInAccount: {}", userEmail, oAuthProviderInRequest, oAuthProviderInAccount);
                throw new EmailAlreadyExistException(userEmail, oAuthProviderInAccount);
            }
        }

        // 존재하지 않으므로 회원 가입
        Account oAuthAccount = Account.createOAuthSignUpMember(userEmail, oAuthProviderInRequest);
        accountRepository.save(oAuthAccount);

        // 토큰 반환
        return getTokenPair(oAuthProviderInRequest, oAuthAccount);
    }

    private TokenPair getTokenPair(OAuthProvider oAuthProviderInRequest, Account oAuthAccount) {
        String accessToken = jwtUtil.createToken(oAuthAccount.getId(), oAuthProviderInRequest, accessValidTime);
        String refreshToken = jwtUtil.createToken(oAuthAccount.getId(), oAuthProviderInRequest, refreshValidTime);

        return new TokenPair(accessToken, refreshToken);
    }


}
