package com.kjh.spacebook.domain.space.dto.response;

import com.kjh.spacebook.domain.space.entity.Space;
import com.kjh.spacebook.domain.space.enums.SpaceType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공간 목록 응답")
public record SpaceListResponse(
        @Schema(description = "공간 ID", example = "1") Long id,
        @Schema(description = "공간 이름", example = "강남 스터디룸 A") String spaceName,
        @Schema(description = "공간 유형") SpaceType spaceType,
        @Schema(description = "수용 인원", example = "4") int capacity,
        @Schema(description = "위치", example = "서울시 강남구") String location,
        @Schema(description = "시간당 가격 (원)", example = "15000") int pricePerHour,
        @Schema(description = "이미지 URL", example = "https://example.com/image.jpg") String imageUrl
) {
    public static SpaceListResponse from(Space space) {
        return new SpaceListResponse(
                space.getId(),
                space.getSpaceName(),
                space.getSpaceType(),
                space.getCapacity(),
                space.getLocation(),
                space.getPricePerHour(),
                space.getImageUrl()
        );
    }
}
