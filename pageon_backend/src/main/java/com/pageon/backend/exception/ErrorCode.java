package com.pageon.backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 회원
    USER_NOT_FOUND("존재하지 않는 사용자입니다.", HttpStatus.NOT_FOUND),
    ROLE_NOT_FOUND("존재하지 않는 권한입니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    PASSWORD_POLICY_VIOLATION("비밀번호는 8자 이상, 영문, 숫자, 특수문자(!@-#$%&^)를 모두 포함해야 합니다.", HttpStatus.BAD_REQUEST),
    OAUTH_PROVIDER_MISMATCH("지원하지 않는 OAuth Provider입니다.", HttpStatus.BAD_REQUEST),
    OAUTH_UNLINK_FAILED("OAuth 연결 해제에 실패했습니다.", HttpStatus.BAD_REQUEST),
    CREATOR_PERMISSION_DENIED("creator 권한이 존재하지 않습니다.", HttpStatus.FORBIDDEN),

    // 본인인증
    INVALID_VERIFICATION_METHOD("지원하지 않는 본인인증 방식입니다.", HttpStatus.BAD_REQUEST),
    IDENTITY_VERIFICATION_ID_NOT_MATCH("전달된 인증 ID가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    OTP_PAYLOAD_NOT_FOUND("OTP 정보가 존재하지 않거나 만료되었습니다.", HttpStatus.NOT_FOUND),
    OTP_NOT_MATCH("전달된 OTP가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    IDENTITY_ALREADY_VERIFIED("이미 본인인증을 완료한 사용자입니다.", HttpStatus.CONFLICT),
    PHONE_NUMBER_ALREADY_VERIFIED("해당 전화번호는 이미 본인인증에 사용되었습니다.", HttpStatus.CONFLICT),

    // 메세지 전송
    MESSAGE_SEND_FAILED("문자 메시지 전송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 작가
    AUTHENTICATION_REQUIRED_TO_REGISTER_AS_CREATOR("작가 등록을 위해서는 본인인증이 필요합니다.", HttpStatus.FORBIDDEN),
    PEN_NAME_REQUIRED("필명은 반드시 입력해야 합니다.", HttpStatus.BAD_REQUEST),
    ALREADY_HAS_CREATOR_ROLE("이미 창작자 권한이 존재합니다.", HttpStatus.BAD_REQUEST),
    AI_POLICY_NOT_AGREED("AI 콘텐츠 등록 약관에 동의하지 않았습니다.", HttpStatus.BAD_REQUEST),
    CREATER_NOT_FOUND("존재하지 않는 작가입니다.", HttpStatus.NOT_FOUND),
    NOT_CREATOR_OF_WEBTOON("웹툰 업로드 권한이 없습니다.", HttpStatus.FORBIDDEN),
    NOT_CREATOR_OF_WEBNOVEL("웹소설 업로드 권한이 없습니다.",  HttpStatus.FORBIDDEN),
    CREATOR_UNAUTHORIZED_ACCESS("해당 콘텐츠의 작성자가 아닙니다.", HttpStatus.FORBIDDEN),

    // 토큰
    TOKEN_GENERATION_FAILED("Refresh Token 또는 Access Token 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    REFRESH_TOKEN_NOT_FOUND("Refresh Token이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    TOKEN_USER_MISMATCH("토큰 사용자 정보가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED),

    // 외부 시스템 오류
    REDIS_CONNECTION_FAILED("Redis 연결에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    MAIL_SEND_FAILED("메일 전송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    S3_UPLOAD_FAILED("S3 업로드 중 오류가 발생했습니다.",HttpStatus.INTERNAL_SERVER_ERROR),
    S3_DELETE_FAILED("S3 데이터 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // file
    FILE_PROCESSING_ERROR("파일의 MIME 타입을 읽는 데 실패했습니다.", HttpStatus.BAD_REQUEST),

    // request
    INVALID_INPUT("입력값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    // 작품
    WEBNOVEL_NOT_FOUND("존재하지 않는 웹소설입니다.", HttpStatus.NOT_FOUND),
    WEBTOON_NOT_FOUND("존재하지 않는 웹툰입니다.", HttpStatus.NOT_FOUND)
    ;


    private final String errorMessage;
    private final HttpStatus httpStatus;

}
