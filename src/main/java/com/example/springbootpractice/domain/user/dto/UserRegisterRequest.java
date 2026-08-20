package com.example.springbootpractice.domain.user.dto;

public record UserRegisterRequest(
        String email,
        String password,
        String phoneNumber
) {
}
