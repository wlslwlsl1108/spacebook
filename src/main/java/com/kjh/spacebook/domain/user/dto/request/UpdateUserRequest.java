package com.kjh.spacebook.domain.user.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        String phoneNumber,

        String currentPassword,

        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        String newPassword
) {}
