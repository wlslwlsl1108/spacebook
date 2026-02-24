package com.kjh.spacebook.domain.auth.controller;

import com.kjh.spacebook.common.response.ApiResponse;
import com.kjh.spacebook.domain.auth.dto.request.DeleteAccountRequest;
import com.kjh.spacebook.domain.auth.dto.request.LoginRequest;
import com.kjh.spacebook.domain.auth.dto.request.SignupRequest;
import com.kjh.spacebook.domain.auth.dto.request.TokenReissueRequest;
import com.kjh.spacebook.domain.auth.dto.response.TokenResponse;
import com.kjh.spacebook.domain.auth.service.AuthService;
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

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TokenResponse>> signup(@Valid @RequestBody SignupRequest request) {
        TokenResponse tokens = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(tokens));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokens = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(tokens));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(@Valid @RequestBody TokenReissueRequest request) {
        TokenResponse tokens = authService.reissue(request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(tokens));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal Long userId) {
        authService.logout(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody DeleteAccountRequest request
    ) {
        authService.deleteAccount(userId, request.password());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null));
    }
}
