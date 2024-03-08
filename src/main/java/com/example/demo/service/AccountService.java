package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.JoinRequest;
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
import java.util.Random;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
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

        Account account = accountRepository.findByEmail(email).orElseThrow(
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

    public void signUp(JoinRequest signUpDto) {
        String name = signUpDto.getName();
        String email = signUpDto.getEmail();
        String encodedPassword = encodePassword(signUpDto.getPassword());

        log.info("회원가입 서비스 함수 호출 name={}, email={}, password={}", name, email, encodedPassword);

        Optional<Account> foundByEmail = accountRepository.findByEmail(email);
        if(foundByEmail.isPresent()){
            log.error("이미 존재하는 이메일이 회원 가입 요청으로 넘어왔다. email: " + email);
            throw new EmailAlreadyExistException(email);
        }

        Account newAccount = Account.createSignUpMember(name, email, encodedPassword);
        accountRepository.save(newAccount);

        log.info("회원가입 서비스 성공 name={}, email={}, password={}", name, email, encodedPassword);
    }

    public boolean isEmailDuplicated(String email) {
        Optional<Account> foundByEmail = accountRepository.findByEmail(email);
        return foundByEmail.isPresent() ? true : false;
    }

    public TokenResponse refresh(String email, String refreshToken) {
        log.info("refresh 요청 접수 email:{}, refreshToken: {}", email, refreshToken);

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
            log.info("refresh 요청 처리 성공 email:{}, refreshToken: {}", email, refreshToken);

            return new TokenResponse(accessToken, refreshToken);
        } catch (Exception ex) {
            throw new TokenRefreshFailException(ex.getMessage(), ex);
        }


    }

    public String emailExistsVerification(String email) {
        log.info("이메일 존재 검증 메서드 호출 email: {}", email);
        String verificationCode = generateRandom6Digit();
        emailService.sendVerificationCode(email, verificationCode);
        return verificationCode;
    }

    private static String generateRandom6Digit(){
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for(int i = 0; i < 6; i++) {
            sb.append((char)(random.nextInt(10) + '0'));
        }

        return sb.toString();
    }

}
