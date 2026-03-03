package com.kjh.spacebook.domain.recommendation.dto.response;

import com.kjh.spacebook.domain.space.enums.SpaceType;

public record SearchCondition(
        String location,
        Integer capacity,
        SpaceType spaceType
) {}
