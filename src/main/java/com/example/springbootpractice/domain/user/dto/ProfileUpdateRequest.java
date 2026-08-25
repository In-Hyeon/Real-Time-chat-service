package com.example.springbootpractice.domain.user.dto;

public record ProfileUpdateRequest(
        String nickname,
        String statusMessage,
        String profileImageUrl
) {
}
