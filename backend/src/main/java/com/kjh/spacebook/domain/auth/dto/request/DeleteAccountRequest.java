package com.kjh.spacebook.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record DeleteAccountRequest(
        @Schema(description = "현재 비밀번호", example = "test1234!!")
        @NotBlank(message = "회원 탈퇴를 위해 비밀번호를 입력해야 합니다.")
        String password
) {}
