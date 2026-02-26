package com.kjh.spacebook.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Schema(description = "새 전화번호", example = "010-9876-5432")
        @Pattern(
                regexp = "^010-\\d{4}-\\d{4}$",
                message = "전화번호 형식은 010-XXXX-XXXX입니다."
        )
        String phoneNumber,

        @Schema(description = "현재 비밀번호 (비밀번호 변경 시 필수)", example = "test1234!!")
        String currentPassword,

        @Schema(description = "새 비밀번호", example = "newPass1234!!")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*]).+$",
                message = "비밀번호는 영문, 숫자, 특수문자(!@#$%^&*)를 각각 1개 이상 포함해야 합니다."
        )
        String newPassword
) {}
