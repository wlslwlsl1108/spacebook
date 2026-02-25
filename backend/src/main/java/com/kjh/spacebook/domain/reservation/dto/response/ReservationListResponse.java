package com.kjh.spacebook.domain.reservation.dto.response;

import com.kjh.spacebook.domain.reservation.entity.Reservation;
import com.kjh.spacebook.domain.reservation.enums.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationListResponse(
        Long id,
        Long spaceId,
        String spaceName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        ReservationStatus status
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
