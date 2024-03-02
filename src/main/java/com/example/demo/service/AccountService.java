package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.SignUpRequest;
import com.example.demo.domain.Account;
import com.example.demo.exception.EmailAlreadyExistException;
import com.example.demo.exception.TokenRefreshFailException;
import com.example.demo.infrastructure.jwt.JwtEntity;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.dto.TokenResponse;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder pe;

    @Value("${message.exception.UsernameNotFoundException}")
    private String userNameNotFoundMsg;

    @Value("${message.exception.BadCredentialsException}")
    private String badCredentialsMsg;
    private final long accessValidTime = 30 * 60 * 1000L;             // 제한시간 30분
    private final long refreshValidTime = 7 * 24 * 60 * 60 * 1000L;    //  제한 시간 일주일

    public TokenResponse login(LoginRequest dto) {

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

        JwtEntity jwtEntity = JwtEntity.createEntityWhenLogin(account.getId(), refreshToken);
        tokenRepository.save(jwtEntity);

        return new TokenResponse(accessToken, refreshToken);
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

    public TokenResponse refresh(String email, String refreshToken) {

        try {
            Account account = accountRepository.findByEmail(email).orElseThrow(
                    () -> new UsernameNotFoundException("User not found with email: " + email)
            );

            JwtEntity jwt = tokenRepository.findByUserPk(account.getId()).orElseThrow(
                    () -> new NoSuchElementException("User doesn't have RefreshToken")
            );

            if (refreshToken.equals(jwt.getRefreshToken())) {
                jwtUtil.validate(refreshToken);
            }

            String accessToken = jwtUtil.createToken(account.getId(), accessValidTime);

            return new TokenResponse(accessToken, refreshToken);
        } catch (Exception ex) {
            throw new TokenRefreshFailException(ex.getMessage(), ex);
        }

    }
}
