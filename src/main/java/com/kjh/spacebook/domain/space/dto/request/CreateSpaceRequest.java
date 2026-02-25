package com.kjh.spacebook.domain.space.dto.request;

import com.kjh.spacebook.domain.space.enums.SpaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateSpaceRequest(
        @NotBlank(message = "공간 이름은 필수입니다.")
        String spaceName,

        @NotBlank(message = "공간 설명은 필수입니다.")
        String description,

        @NotBlank(message = "이미지 URL은 필수입니다.")
        String imageUrl,

        @NotNull(message = "공간 유형은 필수입니다.")
        SpaceType spaceType,

        @NotNull(message = "시간당 가격은 필수입니다.")
        @Positive(message = "시간당 가격은 0보다 커야 합니다.")
        Integer pricePerHour,

        @NotBlank(message = "위치는 필수입니다.")
        String location,

        @NotNull(message = "수용 인원은 필수입니다.")
        @Positive(message = "수용 인원은 0보다 커야 합니다.")
        Integer capacity
) {}
