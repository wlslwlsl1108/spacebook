package com.kjh.spacebook.domain.user.exception;

import com.kjh.spacebook.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    PASSWORD_CHANGE_INCOMPLETE(HttpStatus.BAD_REQUEST, "비밀번호 변경 시 현재 비밀번호와 새 비밀번호를 모두 입력해야 합니다."),
    SAME_PASSWORD(HttpStatus.BAD_REQUEST, "새 비밀번호가 현재 비밀번호와 동일합니다.");

    private final HttpStatus status;
    private final String message;
}
