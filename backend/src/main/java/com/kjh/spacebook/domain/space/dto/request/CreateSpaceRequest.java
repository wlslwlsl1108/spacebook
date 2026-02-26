package com.kjh.spacebook.domain.space.dto.request;

import com.kjh.spacebook.domain.space.enums.SpaceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateSpaceRequest(
        @Schema(description = "공간 이름", example = "강남 스터디룸 A")
        @NotBlank(message = "공간 이름은 필수입니다.")
        String spaceName,

        @Schema(description = "공간 설명", example = "조용하고 쾌적한 4인 스터디룸입니다.")
        @NotBlank(message = "공간 설명은 필수입니다.")
        String description,

        @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
        @NotBlank(message = "이미지 URL은 필수입니다.")
        String imageUrl,

        @Schema(description = "공간 유형")
        @NotNull(message = "공간 유형은 필수입니다.")
        SpaceType spaceType,

        @Schema(description = "시간당 가격 (원)", example = "15000")
        @NotNull(message = "시간당 가격은 필수입니다.")
        @Positive(message = "시간당 가격은 0보다 커야 합니다.")
        Integer pricePerHour,

        @Schema(description = "위치", example = "서울시 강남구")
        @NotBlank(message = "위치는 필수입니다.")
        String location,

        @Schema(description = "수용 인원", example = "4")
        @NotNull(message = "수용 인원은 필수입니다.")
        @Positive(message = "수용 인원은 0보다 커야 합니다.")
        Integer capacity
) {}
