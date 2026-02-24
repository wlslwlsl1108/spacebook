package com.kjh.spacebook.domain.space.dto.response;

import com.kjh.spacebook.domain.space.entity.Space;
import com.kjh.spacebook.domain.space.enums.SpaceStatus;
import com.kjh.spacebook.domain.space.enums.SpaceType;

import java.time.LocalDateTime;

public record SpaceResponse(
        Long id,
        String name,
        String description,
        String imageUrl,
        SpaceType spaceType,
        int pricePerHour,
        String location,
        int capacity,
        SpaceStatus spaceStatus,
        Long ownerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static SpaceResponse from(Space space) {
        return new SpaceResponse(
                space.getId(),
                space.getName(),
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
