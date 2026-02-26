package com.kjh.spacebook.domain.reservation.dto.response;

import com.kjh.spacebook.domain.reservation.entity.Reservation;

public record ReservedTimeResponse(
        int startHour,
        int endHour
) {
    public static ReservedTimeResponse from(Reservation reservation) {
        return new ReservedTimeResponse(
                reservation.getStartTime().getHour(),
                reservation.getEndTime().getHour()
        );
    }
}
