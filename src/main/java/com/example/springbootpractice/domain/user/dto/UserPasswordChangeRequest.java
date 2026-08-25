package com.example.springbootpractice.domain.user.dto;

public record UserPasswordChangeRequest(
        String currentPassword,
        String newPassword
) {
}
