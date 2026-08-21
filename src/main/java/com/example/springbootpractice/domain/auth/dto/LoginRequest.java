package com.example.springbootpractice.domain.auth.dto;

public record LoginRequest(
        String email,
        String password,
        String deviceId
) {
}
