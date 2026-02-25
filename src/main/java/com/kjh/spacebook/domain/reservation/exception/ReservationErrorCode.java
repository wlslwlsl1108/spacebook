package com.kjh.spacebook.domain.reservation.exception;

import com.kjh.spacebook.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReservationErrorCode implements ErrorCode {
    // 조회
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약을 찾을 수 없습니다."),

    // 검증
    RESERVATION_INVALID_TIME(HttpStatus.BAD_REQUEST, "예약 종료 시간은 시작 시간 이후여야 합니다."),
    RESERVATION_PAST_TIME(HttpStatus.BAD_REQUEST, "과거 시간으로는 예약할 수 없습니다."),
    RESERVATION_NOT_HOURLY(HttpStatus.BAD_REQUEST, "예약은 정각 단위로만 가능합니다."),
    RESERVATION_EXCEED_CAPACITY(HttpStatus.BAD_REQUEST, "예약 인원이 공간 최대 수용 인원을 초과합니다."),
    RESERVATION_TIME_CONFLICT(HttpStatus.CONFLICT, "해당 시간대에 이미 예약이 존재합니다."),

    // 권한
    RESERVATION_NOT_OWNER(HttpStatus.FORBIDDEN, "본인의 예약만 접근할 수 있습니다.");

    private final HttpStatus status;
    private final String message;
}
