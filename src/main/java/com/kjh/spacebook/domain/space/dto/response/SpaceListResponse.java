package com.kjh.spacebook.domain.space.dto.response;

import com.kjh.spacebook.domain.space.entity.Space;
import com.kjh.spacebook.domain.space.enums.SpaceType;

public record SpaceListResponse(
        Long id,
        String spaceName,
        SpaceType spaceType,
        int capacity,
        String location,
        int pricePerHour,
        String imageUrl
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
