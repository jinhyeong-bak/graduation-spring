package com.example.demo.exception;

import com.example.demo.dto.AccountLockErrorResponse;
import com.example.demo.dto.ErrorResultResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Hidden
@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {

    @Value("${error.exception.UsernameNotFoundException}")
    private String userNameNotFoundError;
    @Value("${message.exception.UsernameNotFoundException}")
    private String userNameNotFoundMessage;

    @Value("${error.exception.BadCredentialsException}")
    String badCredentialsError;
    @Value("${message.exception.BadCredentialsException}")
    String badCredentialsMessage;


    @ExceptionHandler(value = {UsernameNotFoundException.class, BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResultResponse loginError(AuthenticationException ex) {
        log.info("[로그인 실패 예외 발생]", ex);


        ErrorResultResponse erRspns = null;

        if(ex instanceof UsernameNotFoundException) {
            erRspns = new ErrorResultResponse(userNameNotFoundError, userNameNotFoundMessage);
        }

        if(ex instanceof BadCredentialsException) {
            erRspns = new ErrorResultResponse(badCredentialsError, badCredentialsMessage);
        }

        return erRspns;
    }



    @Value("${error.exception.SignatureException}")
    private String signatureError;
    @Value("${message.exception.SignatureException}")
    private String signatureMessage;


    @Value("${error.exception.ExpiredJwtException}")
    private String expiredJwtError;
    @Value("${message.exception.ExpiredJwtException}")
    private String expiredJwtMessage;

    @Value("${error.exception.TokenRefreshFailException}")
    private String refreshFailError;
    @Value("${error.exception.TokenRefreshFailException}")
    private String refreshFailMessage;


    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResultResponse JwtException(JwtException ex) {
        ErrorResultResponse response = null;

        log.info("Jwt 검증 오류", ex);

        if(ex instanceof SignatureException) {
            response = new ErrorResultResponse(signatureError, signatureMessage);
        }

        if(ex instanceof ExpiredJwtException) {
            response = new ErrorResultResponse(expiredJwtError, expiredJwtMessage);
        }

        if(ex instanceof TokenRefreshFailException) {
            response = new ErrorResultResponse(refreshFailError, refreshFailMessage);
        }

        return response;
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<String> signUpException(RuntimeException ex) {
        String errorMessage = "error: " + ex.getMessage();

        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<AccountLockErrorResponse> lockAccountException(AccountLockedException ex) {
        log.info("lock Account Exception", ex);

        String errorMsg = "비밀번호가 5회 이상 틀려서 계정이 5분간 잠긴 상태입니다.";

        return ResponseEntity.badRequest().body(new AccountLockErrorResponse("AccountLocked", errorMsg, ex.getLockTimeRemainingMillis()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResultResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.info("validation error ", ex);
        ErrorResultResponse response = new ErrorResultResponse("Validation Error", "요청 형식이 올바르지 않습니다.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<ErrorResultResponse> mailSendException(MailException ex) {
        log.info("MailSendException 발생", ex);

        ResponseEntity re = ResponseEntity.internalServerError().build();

        if(ex instanceof MailSendException) {
            ErrorResultResponse response = new ErrorResultResponse("Mail Send Error", "메일을 전송하지 못했습니다.");
            re =  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return re;
    }


}
