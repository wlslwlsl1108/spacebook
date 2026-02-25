package com.kjh.spacebook.domain.user.controller;

import com.kjh.spacebook.common.response.ApiResponse;
import com.kjh.spacebook.domain.user.dto.response.UserResponse;
import com.kjh.spacebook.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyInfo(
            @AuthenticationPrincipal Long userId
    ) {
        UserResponse response = userService.getMyInfo(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response));
    }
}
