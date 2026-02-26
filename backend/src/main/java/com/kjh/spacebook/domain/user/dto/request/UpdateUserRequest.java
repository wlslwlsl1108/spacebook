package com.kjh.spacebook.domain.user.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Pattern(
                regexp = "^010-\\d{4}-\\d{4}$",
                message = "전화번호 형식은 010-XXXX-XXXX입니다."
        )
        String phoneNumber,

        String currentPassword,

        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        @Pattern(
                regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*]).+$",
                message = "비밀번호는 영문, 숫자, 특수문자(!@#$%^&*)를 각각 1개 이상 포함해야 합니다."
        )
        String newPassword
) {}
