package com.kjh.spacebook.domain.user.controller;

import com.kjh.spacebook.common.response.ApiResponse;
import com.kjh.spacebook.domain.user.dto.request.UpdateUserRequest;
import com.kjh.spacebook.domain.user.dto.response.UserResponse;
import com.kjh.spacebook.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "사용자", description = "내 정보 조회 및 수정")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    ) {
        UserResponse response = userService.getMyInfo(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }

    @Operation(summary = "내 정보 수정", description = "전화번호 또는 비밀번호를 수정합니다. null인 필드는 기존 값을 유지합니다.")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateMyInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UserResponse response = userService.updateMyInfo(userId, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }
}
