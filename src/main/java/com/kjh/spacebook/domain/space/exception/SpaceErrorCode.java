package com.kjh.spacebook.domain.space.exception;

import com.kjh.spacebook.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SpaceErrorCode implements ErrorCode {
    SPACE_NOT_FOUND(HttpStatus.NOT_FOUND, "공간을 찾을 수 없습니다."),
    SPACE_CLOSED(HttpStatus.BAD_REQUEST, "현재 대여가 불가능한 공간입니다."),
    INVALID_PRICE_RANGE(HttpStatus.BAD_REQUEST, "최소 가격이 최대 가격보다 클 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
