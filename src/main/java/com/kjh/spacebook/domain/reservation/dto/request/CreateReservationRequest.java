package com.kjh.spacebook.domain.reservation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CreateReservationRequest(
        @NotNull(message = "공간 ID는 필수입니다.")
        Long spaceId,

        @NotNull(message = "예약 시작 시간은 필수입니다.")
        LocalDateTime startTime,

        @NotNull(message = "예약 종료 시간은 필수입니다.")
        LocalDateTime endTime,

        @NotNull(message = "인원 수는 필수입니다.")
        @Positive(message = "인원 수는 0보다 커야 합니다.")
        Integer peopleCount,

        String purpose
) {}
