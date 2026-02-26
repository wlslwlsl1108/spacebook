package com.kjh.spacebook.domain.reservation.dto.response;

import com.kjh.spacebook.domain.reservation.entity.Reservation;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "예약된 시간대 응답")
public record ReservedTimeResponse(
        @Schema(description = "시작 시간 (시)", example = "10") int startHour,
        @Schema(description = "종료 시간 (시)", example = "12") int endHour
) {
    public static ReservedTimeResponse from(Reservation reservation) {
        return new ReservedTimeResponse(
                reservation.getStartTime().getHour(),
                reservation.getEndTime().getHour()
        );
    }
}
