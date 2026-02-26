package com.kjh.spacebook.domain.space.dto.response;

import com.kjh.spacebook.domain.space.entity.Space;
import com.kjh.spacebook.domain.space.enums.SpaceStatus;
import com.kjh.spacebook.domain.space.enums.SpaceType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "공간 상세 응답")
public record SpaceResponse(
        @Schema(description = "공간 ID", example = "1") Long id,
        @Schema(description = "공간 이름", example = "강남 스터디룸 A") String spaceName,
        @Schema(description = "공간 설명", example = "조용하고 쾌적한 4인 스터디룸입니다.") String description,
        @Schema(description = "이미지 URL", example = "https://example.com/image.jpg") String imageUrl,
        @Schema(description = "공간 유형") SpaceType spaceType,
        @Schema(description = "시간당 가격 (원)", example = "15000") int pricePerHour,
        @Schema(description = "위치", example = "서울시 강남구") String location,
        @Schema(description = "수용 인원", example = "4") int capacity,
        @Schema(description = "공간 상태") SpaceStatus spaceStatus,
        @Schema(description = "소유자 ID", example = "1") Long ownerId,
        @Schema(description = "생성일시") LocalDateTime createdAt,
        @Schema(description = "수정일시") LocalDateTime updatedAt
) {
    public static SpaceResponse from(Space space) {
        return new SpaceResponse(
                space.getId(),
                space.getSpaceName(),
                space.getDescription(),
                space.getImageUrl(),
                space.getSpaceType(),
                space.getPricePerHour(),
                space.getLocation(),
                space.getCapacity(),
                space.getSpaceStatus(),
                space.getOwner().getId(),
                space.getCreatedAt(),
                space.getUpdatedAt()
        );
    }
}
