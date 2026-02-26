package com.kjh.spacebook.domain.space.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공간 유형", enumAsRef = true)
public enum SpaceType {
    @Schema(description = "스터디룸") STUDY,
    @Schema(description = "파티룸") PARTY,
    @Schema(description = "회의실") MEETING
}
