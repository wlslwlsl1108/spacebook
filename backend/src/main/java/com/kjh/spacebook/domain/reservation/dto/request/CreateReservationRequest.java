package com.kjh.spacebook.domain.reservation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CreateReservationRequest(
        @Schema(description = "공간 ID", example = "1")
        @NotNull(message = "공간 ID는 필수입니다.")
        Long spaceId,

        @Schema(description = "예약 시작 시간", example = "2026-03-01T10:00:00")
        @NotNull(message = "예약 시작 시간은 필수입니다.")
        LocalDateTime startTime,

        @Schema(description = "예약 종료 시간", example = "2026-03-01T12:00:00")
        @NotNull(message = "예약 종료 시간은 필수입니다.")
        LocalDateTime endTime,

        @Schema(description = "인원 수", example = "3")
        @NotNull(message = "인원 수는 필수입니다.")
        @Positive(message = "인원 수는 0보다 커야 합니다.")
        Integer peopleCount,

        @Schema(description = "예약 목적", example = "팀 프로젝트 회의")
        String purpose
) {}
