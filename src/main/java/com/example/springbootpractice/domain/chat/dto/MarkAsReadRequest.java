package com.example.springbootpractice.domain.chat.dto;

public record MarkAsReadRequest(
        Long lastReadMessageId
) {
}
