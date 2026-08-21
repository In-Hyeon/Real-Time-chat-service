package com.example.springbootpractice.domain.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}
