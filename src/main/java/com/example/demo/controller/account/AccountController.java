package com.example.demo.controller.account;


import com.example.demo.dto.account.request.*;
import com.example.demo.dto.account.response.PasswordVerificationEmail;
import com.example.demo.dto.account.response.TokenPair;
import com.example.demo.dto.account.response.VerificationCodeDto;
import com.example.demo.dto.oauth.OAuthProvider;
import com.example.demo.exception.api.ApiResponse;
import com.example.demo.infrastructure.jwt.JwtUtil;
import com.example.demo.service.AccountService;
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

    @PostMapping("/login")
    public TokenPair login(@RequestBody LoginRequest loginDto) {
        log.info("email={}, password={}", loginDto.getEmail(), loginDto.getPassword());
        return accountService.login(loginDto);
    }

    @PostMapping("/join")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<ApiResponse<String>> signUp(@RequestBody @Valid JoinRequest joinDto) {
        log.info("signUp is called name={}, email={}, password={}", joinDto.getName(), joinDto.getEmail(), joinDto.getPassword());
        accountService.signUp(joinDto);
        return ResponseEntity.ok().body(new ApiResponse<>("회원가입 성공"));
    }


    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<String>> logout(@RequestBody TokenPair tokenPair) {
        log.info("logout 호출 accessToken: {}, refreshToken: {}", tokenPair.getAccessToken(), tokenPair.getRefreshToken());
        accountService.logout(tokenPair);
        return ResponseEntity.ok().body(new ApiResponse<>("로그아웃 성공"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenPair> refresh(@RequestBody RefreshRequest request) {
        TokenPair response =  accountService.refresh(request.getEmail(), request.getRefreshToken());
        log.info("refresh 호출 accessToken: {}, refreshToken: {}", response.getAccessToken(), response.getRefreshToken());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/email/duplicate")
    public ResponseEntity<ApiResponse<String>> emailDuplicate(@RequestBody @Valid EmailDto emailDto) {
        boolean result = accountService.isEmailExists(emailDto.getEmail());
        return result ? ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>("중복된 이메일이 존재합니다."))
                : ResponseEntity.ok().body(new ApiResponse<>("중복된 이메일이 존재하지 않습니다."));
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

        long accessValidTime = 300000;  // 5분
        String verificationToken = jwtUtil.createToken(-1L, OAuthProvider.SELF, accessValidTime);

        PasswordVerificationEmail response = new PasswordVerificationEmail();
        response.setVerificationCode(verificationCode);
        response.setVerificationToken(verificationToken);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/password/renewal")
    public ResponseEntity<ApiResponse<String>> renewalPassword(@RequestBody @Valid NewPassword newPassword){
        jwtUtil.validate(newPassword.getVerificationToken());
        accountService.renewalPassword(newPassword);
        return ResponseEntity.ok().body(new ApiResponse<>("비밀번호 갱신에 성공하였습니다."));
    }


}
