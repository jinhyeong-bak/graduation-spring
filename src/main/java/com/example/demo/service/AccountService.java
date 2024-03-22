package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignUpRequest;
import com.example.demo.domain.Account;
import com.example.demo.exception.EmailAlreadyExistException;
import com.example.demo.exception.TokenBlackListedException;
import com.example.demo.exception.TokenRefreshFailException;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.dto.TokenPair;
import com.example.demo.infrastructure.jwt.RedisToken;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.RedisTokenRepository;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final RedisTokenRepository redisTokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder pe;

    @Value("${message.exception.UsernameNotFoundException}")
    private String userNameNotFoundMsg;

    @Value("${message.exception.BadCredentialsException}")
    private String badCredentialsMsg;
    private final long accessValidTime = 30 * 60 * 1000L;                    // 제한시간 30분
    private final long refreshValidTime = 12 * 30 * 24 * 60 * 60 * 1000L;    //  제한 시간 일년

    public TokenPair login(LoginRequest dto) {

        String email = dto.getEmail();
        String password = dto.getPassword();

        log.info("login 함수 호출 id = {}", email);

        Account account = accountRepository.findByEmail(dto.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException(userNameNotFoundMsg + ": " + email)
        );


        if(!comparePassword(account.getPassword(), password)) {
            log.info("login password 검증 실패 id = {}, password={}", email, password);
            throw new BadCredentialsException(badCredentialsMsg);
        }

        log.info("login password 검증 성공 id = {}", email);

        String accessToken = jwtUtil.createToken(account.getId(), accessValidTime);
        String refreshToken = jwtUtil.createToken(account.getId(), refreshValidTime);

        RedisToken refreshTokenRecord = RedisToken.createTokenInRedis(refreshToken, refreshValidTime);
        redisTokenRepository.save(refreshTokenRecord);

        return new TokenPair(accessToken, refreshToken);
    }

    private boolean comparePassword(String fromDb, String fromRequest) {
        return pe.matches(fromRequest, fromDb);
    }
    private String encodePassword(String password) {
        return pe.encode(password);
    }

    public void signUp(SignUpRequest signUpDto) {
        String name = signUpDto.getName();
        String email = signUpDto.getEmail();
        String encodedPassword = encodePassword(signUpDto.getPassword());

        log.info("회원가입 서비스 함수 호출 name={}, email={}, password={}", name, email, encodedPassword);

        Optional<Account> foundByEmail = accountRepository.findByEmail(email);
        if(foundByEmail.isPresent()){
            log.error("이미 존재하는 이메일이 회원가입 요청으로 넘어왔다. email: " + email);
            throw new EmailAlreadyExistException(email);
        }

        Account newAccount = Account.createSignUpMember(name, email, encodedPassword);
        accountRepository.save(newAccount);

        log.info("회원가입 서비스 성공 name={}, email={}, password={}", name, email, encodedPassword);
    }

    public TokenPair refresh(String email, String refreshToken) {

        try {
            Account account = accountRepository.findByEmail(email).orElseThrow(
                    () -> new UsernameNotFoundException("User not found with email: " + email)
            );

            jwtUtil.validate(refreshToken);

            if(redisTokenRepository.findById(refreshToken).isEmpty()) {
                throw new TokenBlackListedException("버려진 refreshToken으로부터 refresh요청이 왔습니다. token: " + refreshToken);
            }

            String accessToken = jwtUtil.createToken(account.getId(), accessValidTime);


            return new TokenPair(accessToken, refreshToken);
        } catch (Exception ex) {
            throw new TokenRefreshFailException(ex.getMessage(), ex);
        }

    }

    public void logout(TokenPair tokenPair) {
        log.info("AccountService logout 호출 accessToken: {}, refreshToken:{} ", tokenPair.getAccessToken(), tokenPair.getRefreshToken());
        String accessToken = tokenPair.getAccessToken();
        String refreshToken = tokenPair.getRefreshToken();

        long ttl = jwtUtil.getExpiredDate(accessToken).getTime() - new Date().getTime();

        RedisToken accessTokenRecord = RedisToken.createTokenInRedis(accessToken, ttl);

        redisTokenRepository.save(accessTokenRecord);
        redisTokenRepository.deleteById(refreshToken);
    }
}
