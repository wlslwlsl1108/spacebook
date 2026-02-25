package com.kjh.spacebook.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DeleteAccountRequest(
        @NotBlank(message = "회원 탈퇴를 위해 비밀번호를 입력해야 합니다.")
        String password
) {}
