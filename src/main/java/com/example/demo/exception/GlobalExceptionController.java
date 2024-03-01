package com.example.demo.exception;

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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
}
