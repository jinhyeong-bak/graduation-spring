package com.example.demo.controller;


import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RefreshRequest;
import com.example.demo.dto.SignUpRequest;
import com.example.demo.dto.TokenResponse;
import com.example.demo.service.AccountService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "계정 컨트롤러", description = "로그인, 로그아웃, 회원가입을 포함한 모든 계정 관련 APIs")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService loginService;


    @PostMapping("/login")
    @ApiResponse(responseCode = "401", ref = "loginEx")
    public TokenResponse login(@RequestBody LoginRequest loginDto) {
        log.info("email={}, password={}", loginDto.getEmail(), loginDto.getPassword());
        return loginService.login(loginDto);
    }

    @PostMapping("/join")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiResponse(responseCode = "409", description = "Email already Exist")
    public void signUp(@RequestBody SignUpRequest signUpDto) {
        log.info("signUp is called name={}, email={}, password={}", signUpDto.getName(), signUpDto.getEmail(), signUpDto.getPassword());
        loginService.signUp(signUpDto);
    }

    @PostMapping("/refresh")
    @ApiResponse(responseCode = "401", ref = "refreshEx")
    public TokenResponse refresh(@RequestBody RefreshRequest request) {
        return loginService.refresh(request.getEmail(), request.getRefreshToken());
    }


}
