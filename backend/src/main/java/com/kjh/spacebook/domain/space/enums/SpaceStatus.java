package com.kjh.spacebook.domain.space.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공간 상태", enumAsRef = true)
public enum SpaceStatus {
    @Schema(description = "운영 중") OPEN,
    @Schema(description = "운영 중단") CLOSED
}
