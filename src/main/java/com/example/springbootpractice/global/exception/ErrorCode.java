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
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않거나 참여하지 않은 채팅방입니다."),
    INVALID_MESSAGE_TYPE(HttpStatus.BAD_REQUEST, "허용되지 않는 메시지 타입입니다."),
    INVALID_MESSAGE_CONTENT(HttpStatus.BAD_REQUEST, "메시지 내용이 올바르지 않습니다."),
    MESSAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 메시지입니다."),
    MESSAGE_ALREADY_DELETED(HttpStatus.CONFLICT, "이미 삭제된 메시지입니다."),
    SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "구독 내역이 없습니다."),
    EMOJI_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 이모티콘입니다."),
    EMOJI_ALREADY_OWNED(HttpStatus.CONFLICT, "이미 보유한 이모티콘입니다."),
    EMOJI_NOT_USABLE(HttpStatus.FORBIDDEN, "보유하지 않았고 구독 중도 아니라 사용할 수 없는 이모티콘입니다."),
    GIFT_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 선물 상품입니다."),
    CANNOT_GIFT_SELF(HttpStatus.BAD_REQUEST, "자기 자신에게는 선물할 수 없습니다."),
    GIFT_ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 선물 주문입니다."),
    VOUCHER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 교환권입니다."),
    VOUCHER_ALREADY_USED(HttpStatus.CONFLICT, "이미 사용되었거나 만료된 교환권입니다."),
    DEVICE_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 디바이스 토큰입니다."),
    INVALID_DEVICE_TYPE(HttpStatus.BAD_REQUEST, "허용되지 않는 디바이스 타입입니다."),
    INVALID_CALL_TYPE(HttpStatus.BAD_REQUEST, "허용되지 않는 통화 타입입니다."),
    INVALID_CALL_STATUS(HttpStatus.BAD_REQUEST, "허용되지 않는 통화 상태 값입니다.");

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
