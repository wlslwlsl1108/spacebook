package com.kjh.spacebook.domain.space.dto.request;

import com.kjh.spacebook.domain.space.enums.SpaceStatus;
import com.kjh.spacebook.domain.space.enums.SpaceType;
import jakarta.validation.constraints.Positive;

public record UpdateSpaceRequest(
        String spaceName,
        String description,
        String imageUrl,
        SpaceType spaceType,

        @Positive(message = "시간당 가격은 0보다 커야 합니다.")
        Integer pricePerHour,

        String location,

        @Positive(message = "수용 인원은 0보다 커야 합니다.")
        Integer capacity,

        SpaceStatus spaceStatus
) {}
