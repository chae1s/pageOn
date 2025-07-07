package com.pageon.backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 회원
    USER_NOT_FOUND("존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND("기본 권한이 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSWORD_POLICY_VIOLATION("비밀번호는 8자 이상, 영문, 숫자, 특수문자(!@-#$%&^)를 모두 포함해야 합니다.", HttpStatus.BAD_REQUEST),
    OAUTH_PROVIDER_MISMATCH("지원하지 않는 OAuth Provider입니다.", HttpStatus.BAD_REQUEST),
    OAUTH_UNLINK_FAILED("OAuth 연결 해제에 실패했습니다.", HttpStatus.BAD_REQUEST),

    // 토큰
    REFRESH_TOKEN_NOT_FOUND("Refresh Token이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_USER_MISMATCH("토큰 사용자 정보가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),
    ;

    private final String errorMessage;
    private final HttpStatus httpStatus;

}
