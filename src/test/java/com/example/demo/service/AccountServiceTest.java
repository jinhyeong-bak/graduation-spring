package com.example.demo.service;

import com.example.demo.domain.account.Account;
import com.example.demo.dto.account.request.LoginRequest;
import com.example.demo.dto.account.response.TokenPair;
import com.example.demo.dto.account.request.JoinRequest;

import com.example.demo.dto.oauth.OAuthProvider;
import com.example.demo.exception.EmailAlreadyExistException;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.repository.account.AccountRepository;
import com.example.demo.repository.account.RedisTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    JwtUtil jwtUtil;
    PasswordEncoder pe;
    @Mock
    RedisTokenRepository tokenRepository;
    @Mock
    AccountRepository accountRepository;
    @InjectMocks
    AccountService accountService;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(JwtUtil.createKey());
        pe = new BCryptPasswordEncoder();

        ReflectionTestUtils.setField(accountService, "jwtUtil", jwtUtil);
        ReflectionTestUtils.setField(accountService, "pe", pe);
        ReflectionTestUtils.setField(accountService, "userNameNotFoundMsg", "User not found with email: ");
        ReflectionTestUtils.setField(accountService, "badCredentialsMsg", "Bad credentials");
    }

    @Test
    void login_withValidEmail_returnsTokenResponse() {
        String email = "user@test.com";
        String password = "0000";

        Account account = new Account();
        ReflectionTestUtils.setField(account, "id", 1L);
        ReflectionTestUtils.setField(account, "email", email);
        ReflectionTestUtils.setField(account, "password", pe.encode(password));
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));

        LoginRequest dto = new LoginRequest();
        dto.setEmail(email);
        dto.setPassword(password);

        TokenPair tokenResponse = accountService.login(dto);

        assertThat(jwtUtil.getUserPk(tokenResponse.getAccessToken())).isEqualTo(1L);
        assertThat(jwtUtil.validate(tokenResponse.getAccessToken())).isTrue();
        assertThat(jwtUtil.validate(tokenResponse.getRefreshToken())).isTrue();
    }

    @Test
    void login_withIncorrectPassword_returnsBadCredentialException() {
        String email = "user@test.com";
        String password = "0000";

        Account account = new Account();
        ReflectionTestUtils.setField(account, "id", 1L);
        ReflectionTestUtils.setField(account, "email", email);
        ReflectionTestUtils.setField(account, "password", pe.encode(password));
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));

        LoginRequest dto = new LoginRequest();
        dto.setEmail(email);
        dto.setPassword(password + "1");

        assertThatThrownBy(
                () -> accountService.login(dto)
        ).isInstanceOf(BadCredentialsException.class);
    }

    @Test
    void login_withNonExistEmail_throwsUserNotFoundException() {
        String email = "user@test.com";
        String password = "0000";

        Account account = new Account();
        ReflectionTestUtils.setField(account, "id", 1L);
        ReflectionTestUtils.setField(account, "email", email);
        ReflectionTestUtils.setField(account, "password", pe.encode(password));
        when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());

        LoginRequest dto = new LoginRequest();
        dto.setEmail(email);
        dto.setPassword(password + "1");

        assertThatThrownBy(
                () -> accountService.login(dto)
        ).isInstanceOf(UsernameNotFoundException.class);
    }

    @Test
    void signUp_withAlreadyExistEmail_throwsEmailAlreadyExistException() {
        // set up
        String name = "user";
        String email = "user@test.com";
        String password = "0000";

        Account account = new Account();
        ReflectionTestUtils.setField(account, "email", email);
        ReflectionTestUtils.setField(account, "password", password);
        ReflectionTestUtils.setField(account, "name", name);

        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));

        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setEmail(email);
        joinRequest.setPassword(password);

        // test
        assertThatThrownBy(
                () -> accountService.signUp(joinRequest)
        ).isInstanceOf(EmailAlreadyExistException.class);
    }

    @Test
    void refresh_withValidEmailAndRefreshToken_returnsTokenResponse() {
        //given
        Long userPk = 0L;
        String email = "user@test.com";
        String refreshToken = jwtUtil.createToken(userPk, OAuthProvider.SELF,1000L * 60 * 60);

        Account account = new Account();
        ReflectionTestUtils.setField(account, "id", userPk);
        ReflectionTestUtils.setField(account, "email", email);

        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(account));

        //when
        TokenPair tokenResponse = accountService.refresh(email, refreshToken);

        String accessToken = tokenResponse.getAccessToken();

        //then
        assertThat(jwtUtil.getUserPk(accessToken)).isEqualTo(userPk);
        assertThat(jwtUtil.validate(accessToken)).isTrue();
        assertThat(tokenResponse.getRefreshToken()).isEqualTo(refreshToken);
    }

    @Test
    void isEmailDuplicated_withAlreadyExistEmail_returnsTrue() {
        //given
        String email = "test@test.com";
        when(accountRepository.findByEmail(email)).thenReturn(Optional.of(new Account()));

        //when
        boolean result = accountService.isEmailExists(email);

        //then
        assertThat(result).isTrue();

    }

    @Test
    void isEmailDuplicated_withNonExistEmail_returnsFalse() {
        //given
        String email = "test@test.com";
        when(accountRepository.findByEmail(email)).thenReturn(Optional.empty());

        //when
        boolean result = accountService.isEmailExists(email);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void emailExistsVerification() {
    }
}