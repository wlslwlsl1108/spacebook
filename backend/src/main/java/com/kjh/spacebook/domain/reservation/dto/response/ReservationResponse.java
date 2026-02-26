package com.kjh.spacebook.domain.reservation.dto.response;

import com.kjh.spacebook.domain.reservation.entity.Reservation;
import com.kjh.spacebook.domain.reservation.enums.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "예약 상세 응답")
public record ReservationResponse(
        @Schema(description = "예약 ID", example = "1") Long id,
        @Schema(description = "공간 ID", example = "1") Long spaceId,
        @Schema(description = "공간 이름", example = "강남 스터디룸 A") String spaceName,
        @Schema(description = "사용자 ID", example = "1") Long userId,
        @Schema(description = "예약 시작 시간") LocalDateTime startTime,
        @Schema(description = "예약 종료 시간") LocalDateTime endTime,
        @Schema(description = "인원 수", example = "3") int peopleCount,
        @Schema(description = "총 가격 (원)", example = "30000") int totalPrice,
        @Schema(description = "예약 목적", example = "팀 프로젝트 회의") String purpose,
        @Schema(description = "예약 상태") ReservationStatus status,
        @Schema(description = "생성일시") LocalDateTime createdAt
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
