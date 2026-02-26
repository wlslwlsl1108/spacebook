package com.kjh.spacebook.domain.reservation.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "예약 상태", enumAsRef = true)
public enum ReservationStatus {
    @Schema(description = "확정") CONFIRMED,
    @Schema(description = "취소됨") CANCELLED
}
