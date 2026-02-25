package com.kjh.spacebook.domain.recommendation.exception;

import com.kjh.spacebook.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum RecommendationErrorCode implements ErrorCode {
    AI_PARSE_FAILED(HttpStatus.BAD_REQUEST, "검색 조건을 추출할 수 없습니다. 다시 입력해주세요."),
    AI_API_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "AI 서비스에 일시적인 문제가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
