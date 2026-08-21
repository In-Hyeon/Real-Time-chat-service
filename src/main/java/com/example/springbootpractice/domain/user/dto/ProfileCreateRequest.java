package com.example.springbootpractice.domain.user.dto;

public record ProfileCreateRequest(
        String nickname,
        String profileType
) {
}
