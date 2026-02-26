package com.kjh.spacebook.domain.auth.controller;

import com.kjh.spacebook.common.response.ApiResponse;
import com.kjh.spacebook.domain.auth.dto.request.DeleteAccountRequest;
import com.kjh.spacebook.domain.auth.dto.request.LoginRequest;
import com.kjh.spacebook.domain.auth.dto.request.SignupRequest;
import com.kjh.spacebook.domain.auth.dto.request.TokenReissueRequest;
import com.kjh.spacebook.domain.auth.dto.response.TokenResponse;
import com.kjh.spacebook.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증", description = "회원가입, 로그인, 토큰 재발급, 로그아웃, 회원 탈퇴")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 인증

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자의 리프레시 토큰을 무효화합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    ) {
        authService.logout(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }

    @Operation(summary = "회원 탈퇴", description = "비밀번호 확인 후 회원 탈퇴를 진행합니다. (Soft Delete)")
    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Valid @RequestBody DeleteAccountRequest request
    ) {
        authService.deleteAccount(userId, request.password());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }

    // 공개

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 이름, 전화번호로 회원가입합니다. 성공 시 액세스/리프레시 토큰을 반환합니다.",
            security = {})
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TokenResponse>> signup(@Valid @RequestBody SignupRequest request) {
        TokenResponse tokens = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(tokens));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다. 성공 시 액세스/리프레시 토큰을 반환합니다.",
            security = {})
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokens = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(tokens));
    }

    @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 새로운 액세스/리프레시 토큰을 발급받습니다.",
            security = {})
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(@Valid @RequestBody TokenReissueRequest request) {
        TokenResponse tokens = authService.reissue(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(tokens));
    }
}
