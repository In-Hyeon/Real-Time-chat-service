package com.example.springbootpractice.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.CONFLICT, "이미 가입된 전화번호입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않거나 만료된 토큰입니다."),
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 프로필입니다."),
    PROFILE_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "프로필은 최대 3개까지 생성할 수 있습니다."),
    CANNOT_FRIEND_SELF(HttpStatus.BAD_REQUEST, "자기 자신은 친구로 추가할 수 없습니다."),
    ALREADY_FRIEND(HttpStatus.CONFLICT, "이미 친구로 추가된 사용자입니다."),
    FRIEND_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 친구 관계입니다."),
    INVALID_FRIEND_STATUS(HttpStatus.BAD_REQUEST, "허용되지 않는 친구 상태 값입니다."),
    INVALID_ROOM_TYPE(HttpStatus.BAD_REQUEST, "허용되지 않는 채팅방 타입입니다."),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않거나 참여하지 않은 채팅방입니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
