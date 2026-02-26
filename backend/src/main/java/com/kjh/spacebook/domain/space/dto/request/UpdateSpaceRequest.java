package com.kjh.spacebook.domain.space.dto.request;

import com.kjh.spacebook.domain.space.enums.SpaceStatus;
import com.kjh.spacebook.domain.space.enums.SpaceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

public record UpdateSpaceRequest(
        @Schema(description = "공간 이름", example = "강남 스터디룸 B")
        String spaceName,

        @Schema(description = "공간 설명", example = "리모델링된 스터디룸입니다.")
        String description,

        @Schema(description = "이미지 URL", example = "https://example.com/new-image.jpg")
        String imageUrl,

        @Schema(description = "공간 유형")
        SpaceType spaceType,

        @Schema(description = "시간당 가격 (원)", example = "20000")
        @Positive(message = "시간당 가격은 0보다 커야 합니다.")
        Integer pricePerHour,

        @Schema(description = "위치", example = "서울시 서초구")
        String location,

        @Schema(description = "수용 인원", example = "6")
        @Positive(message = "수용 인원은 0보다 커야 합니다.")
        Integer capacity,

        @Schema(description = "공간 상태")
        SpaceStatus spaceStatus
) {}
