package com.example.demo.controller;


import com.example.demo.dto.*;
import com.example.demo.dto.oauth.OAuthProvider;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.service.AccountService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "계정 컨트롤러", description = "로그인, 로그아웃, 회원가입을 포함한 모든 계정 관련 APIs")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;
    private final JwtUtil jwtUtil;

    @Value("${security.jwt.validTime.accessToken}")
    private long accessValidTime;           // 제한시간 30분

    @PostMapping("/login")
    @ApiResponse(responseCode = "401", ref = "loginEx")
    public TokenResponse login(@RequestBody LoginRequest loginDto) {
        log.info("email={}, password={}", loginDto.getEmail(), loginDto.getPassword());
        return accountService.login(loginDto);
    }

    @PostMapping("/join")
    @ResponseStatus(code = HttpStatus.OK)
    @ApiResponse(responseCode = "409", description = "Email already Exist")
    public void signUp(@RequestBody @Valid JoinRequest joinDto) {
        log.info("signUp is called name={}, email={}, password={}", joinDto.getName(), joinDto.getEmail(), joinDto.getPassword());
        accountService.signUp(joinDto);
    }

    @PostMapping("/refresh")
    @ApiResponse(responseCode = "401", ref = "refreshEx")
    @ResponseBody
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest request) {
        TokenResponse response =  accountService.refresh(request.getEmail(), request.getRefreshToken());
        log.info("refresh 호출 accessToken: {}, refreshToken: {}", response.getAccessToken(), response.getRefreshToken());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/email/duplicate")
    public ResponseEntity emailDuplicate(@RequestBody @Valid EmailDto emailDto) {
        boolean result = accountService.isEmailExists(emailDto.getEmail());
        return result ? ResponseEntity.status(HttpStatus.CONFLICT).build() : ResponseEntity.ok().build();
    }

    @PostMapping("/email/verification")
    public ResponseEntity<VerificationCodeDto> emailVerification(@RequestBody @Valid EmailDto emailDto){
        String verificationCode = accountService.emailExistsVerification(emailDto.getEmail());
        VerificationCodeDto verificationCodeDto = new VerificationCodeDto(verificationCode);
        return ResponseEntity.ok().body(verificationCodeDto);
    }

    @PostMapping("/password/verificationEmail")
    public ResponseEntity<PasswordVerificationEmail> findPassword(@RequestBody @Valid EmailDto emailDto){
        String verificationCode = accountService.findPassword(emailDto.getEmail());

        String verificationToken = jwtUtil.createToken(-1L, OAuthProvider.SELF,accessValidTime);
        PasswordVerificationEmail response = new PasswordVerificationEmail();
        response.setVerificationCode(verificationCode);
        response.setVerificationToken(verificationToken);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/password/renewal")
    public ResponseEntity renewalPassword(@RequestBody @Valid NewPassword newPassword){
        jwtUtil.validate(newPassword.getVerificationToken());
        accountService.renewalPassword(newPassword);
        return ResponseEntity.ok().build();
    }


}
