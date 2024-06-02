package com.example.demo.service;

import com.example.demo.dto.account.request.LoginRequest;
import com.example.demo.dto.account.request.JoinRequest;
import com.example.demo.domain.account.Account;
import com.example.demo.dto.account.request.NewPassword;
import com.example.demo.dto.oauth.OAuthProvider;
import com.example.demo.exception.AccountLockedException;
import com.example.demo.exception.EmailAlreadyExistException;
import com.example.demo.exception.TokenBlackListedException;
import com.example.demo.exception.TokenRefreshFailException;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.dto.account.response.TokenPair;
import com.example.demo.infrastructure.jwt.RedisToken;
import com.example.demo.repository.account.AccountRepository;
import com.example.demo.repository.account.RedisTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final RedisTokenRepository redisTokenRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder pe;

    @Value("${message.exception.UsernameNotFoundException}")
    private String userNameNotFoundMsg;

    @Value("${message.exception.BadCredentialsException}")
    private String badCredentialsMsg;

    @Value("${security.jwt.validTime.accessToken}")
    private long accessValidTime;           // 제한시간 30분
    @Value("${security.jwt.validTime.refreshToken}")
    private long refreshValidTime;          // 제한 시간 일년
    private final int loginAttemptLimit = 5;
    private final int LockPeriodMinutes = 5;

    @Transactional(noRollbackFor = {AccountLockedException.class, BadCredentialsException.class})
    public TokenPair login(LoginRequest dto) {

        String email = dto.getEmail();
        String password = dto.getPassword();

        log.info("login 함수 호출 id = {}", email);

        Account account = accountRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(userNameNotFoundMsg + ": " + email)
        );

        log.info("계정 잠금 시간 확인: " +  account.getLoginLockTime());
        LocalDateTime accountUnlockTime = account.getLoginLockTime().plusMinutes(LockPeriodMinutes);

        // 연속 시도는 24시간 안에만 유효
        if(Duration.between(account.getLoginLastTryTime(), LocalDateTime.now()).toMillis() > 1000L * 60 * 60 * 24) {
            log.info("마지막 로그인 시도로부터 24시간이 지나서 시간 및 횟수 초기화 email: {}, 마지막 시간: {}, 현재 시간: {}", email, account.getLoginLastTryTime(), LocalDateTime.now());
            account.initWrongPasswordCount();
        }

        // 마지막 로그인 시도 시간 갱신
        account.renewLoginLastTryTime();

        // 아이디가 잠긴 상태인지 확인
        long differenceInMillis = Duration.between(LocalDateTime.now(), accountUnlockTime).toMillis();
        if(differenceInMillis > 0) {
            throw new AccountLockedException("email: " + email + " 비밀번호 오류 5회로 계정 잠금 남은 시간: " + differenceInMillis, differenceInMillis);
        }

        // 비밀번호 확인
        if(!comparePassword(account.getPassword(), password)) {
            log.info("login password 검증 실패 id = {}, password={}", email, password);
            Integer wrongPasswordCount = account.getWrongPasswordCount();
            log.info("wrongPasswordCount: {}", wrongPasswordCount);
            if(wrongPasswordCount + 1 == loginAttemptLimit) {
                account.LockAccount();
                account.initWrongPasswordCount();
                throw new AccountLockedException("email: " + email + " 비밀번호 오류 5회로 계정 잠금 남은 시간: " +  5 * 60 * 1000L, 5 * 60 * 1000L);
            }
            else {
                account.incWrongPasswordCount();
            }
            throw new BadCredentialsException(badCredentialsMsg);
        }

        log.info("login password 검증 성공 id = {}", email);
        account.initWrongPasswordCount();
        String accessToken = jwtUtil.createToken(account.getId(), OAuthProvider.SELF, accessValidTime);
        String refreshToken = jwtUtil.createToken(account.getId(), OAuthProvider.SELF, refreshValidTime);

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

    public void signUp(JoinRequest signUpDto) {
        String name = signUpDto.getName();
        String email = signUpDto.getEmail();
        String encodedPassword = encodePassword(signUpDto.getPassword());

        log.info("회원가입 서비스 함수 호출 name={}, email={}, password={}", name, email, encodedPassword);

        Optional<Account> foundByEmail = accountRepository.findByEmail(email);
        if(foundByEmail.isPresent()){
            Account account = foundByEmail.get();
            OAuthProvider oAuthProvider = account.getOAuthProvider();
            log.error("이미 존재하는 이메일이 회원 가입 요청으로 넘어왔다. email: {}, OAuthProvider: {}", email, oAuthProvider);
            throw new EmailAlreadyExistException(email,  oAuthProvider);
        }

        Account newAccount = Account.createSignUpMember(name, email, encodedPassword, OAuthProvider.SELF);
        accountRepository.save(newAccount);

        log.info("회원가입 서비스 성공 name={}, email={}, password={}", name, email, encodedPassword);
        log.info("계정 잠금 시간 확인: " +  newAccount.getLoginLockTime());
    }

    public boolean isEmailExists(String email) {
        Optional<Account> foundByEmail = accountRepository.findByEmail(email);
        return foundByEmail.isPresent() ? true : false;
    }

    public TokenPair refresh(String email, String refreshToken) {
        log.info("refresh 요청 접수 email:{}, refreshToken: {}", email, refreshToken);

        try {
            String oAuthProviderStr = jwtUtil.getSpecifiedClaim(refreshToken, "oAuthProvider", String.class);
            OAuthProvider oAuthProvider = Enum.valueOf(OAuthProvider.class, oAuthProviderStr);
            long userPk = jwtUtil.getUserPk(refreshToken);

            jwtUtil.validate(refreshToken);

            if(redisTokenRepository.findById(refreshToken).isEmpty()) {
                throw new TokenBlackListedException("버려진 refreshToken으로부터 refresh요청이 왔습니다. token: " + refreshToken);
            }

            Account account = accountRepository.findByEmail(email).orElseThrow(
                    () -> new UsernameNotFoundException("User not found with email: " + email)
            );

            String accessToken = jwtUtil.createToken(userPk, oAuthProvider, accessValidTime);

            redisTokenRepository.deleteById(refreshToken);
            refreshToken = jwtUtil.createToken(userPk, oAuthProvider, refreshValidTime);
            RedisToken refreshTokenInRedis = RedisToken.createTokenInRedis(refreshToken, refreshValidTime);
            redisTokenRepository.save(refreshTokenInRedis);

            log.info("refresh 요청 처리 성공 email:{}, refreshToken: {}", email, refreshToken);


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

    public String findPassword(String email) {
        log.info("비밀번호 찾기 메서드 호출 email: {}", email);

        String subject = "dripMid 비밀번호 찾기 안내 메일";
        String body = "";
        String verificationCode = generateRandom6Digit();

        if(!isEmailExists(email)) {
            body = "dripMind 비밀번호 찾기 안내 메일입니다."
                    + "\r\n"
                    + email +"로 가입된 아이디가 없습니다.\r\n"
                    + "\r\n";
        }
        else {
            body = "dripMind 비밀번호 찾기 안내 메일입니다."
                    + "\r\n"
                    + "사용자가 본인임을 확인하려고 합니다. 메시지가 표시되면 다음 확인 코드를 입력하세요.\r\n"
                    + "\r\n"
                    + "확인 코드: "
                    + verificationCode
                    + "\r\n";
        }

        emailService.sendEmail(email, subject, body);
        return verificationCode;
    }

    public String emailExistsVerification(String email) {
        log.info("이메일 존재 검증 메서드 호출 email: {}", email);
        String verificationCode = generateRandom6Digit();

        String subject = "dripMid 회원가입 확인 메일";
        String body = "새로운 dripMind 계정 생성 프로세스를 시작해 주셔서 감사합니다."
                + "\r\n"
                + "사용자가 본인임을 확인하려고 합니다. 메시지가 표시되면 다음 확인 코드를 입력하세요.\r\n"
                + "\r\n"
                + "확인 코드: "
                + verificationCode
                + "\r\n";

        emailService.sendEmail(email, subject, body);
        return verificationCode;
    }

    public void renewalPassword(NewPassword newPassword) {

        String email = newPassword.getEmail();
        String password = newPassword.getPassword();
        String encodedPassword = encodePassword(password);

        log.info("비밀번호 갱신 메서드 호출 email: {}, password: {}, encodedPassword: {}", email, password, encodedPassword);

        Account account = accountRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User not found with email: " + email)
        );

        account.renewalPassword(encodedPassword);
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
