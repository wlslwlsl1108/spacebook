package com.kjh.spacebook.domain.reservation.dto.response;

import com.kjh.spacebook.domain.reservation.entity.Reservation;
import com.kjh.spacebook.domain.reservation.enums.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "예약 목록 응답")
public record ReservationListResponse(
        @Schema(description = "예약 ID", example = "1") Long id,
        @Schema(description = "공간 ID", example = "1") Long spaceId,
        @Schema(description = "공간 이름", example = "강남 스터디룸 A") String spaceName,
        @Schema(description = "예약 시작 시간") LocalDateTime startTime,
        @Schema(description = "예약 종료 시간") LocalDateTime endTime,
        @Schema(description = "예약 상태") ReservationStatus status
) {
    public static ReservationListResponse from(Reservation reservation) {
        return new ReservationListResponse(
                reservation.getId(),
                reservation.getSpace().getId(),
                reservation.getSpace().getSpaceName(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus()
        );
    }
}
