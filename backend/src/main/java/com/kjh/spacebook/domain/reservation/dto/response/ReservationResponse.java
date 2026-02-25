package com.kjh.spacebook.domain.reservation.dto.response;

import com.kjh.spacebook.domain.reservation.entity.Reservation;
import com.kjh.spacebook.domain.reservation.enums.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Long spaceId,
        String spaceName,
        Long userId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int peopleCount,
        int totalPrice,
        String purpose,
        ReservationStatus status,
        LocalDateTime createdAt
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getSpace().getId(),
                reservation.getSpace().getSpaceName(),
                reservation.getUser().getId(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getPeopleCount(),
                reservation.getTotalPrice(),
                reservation.getPurpose(),
                reservation.getStatus(),
                reservation.getCreatedAt()
        );
    }
}
