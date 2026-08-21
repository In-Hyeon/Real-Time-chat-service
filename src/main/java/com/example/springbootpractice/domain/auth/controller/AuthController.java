package com.example.springbootpractice.domain.auth.controller;

import com.example.springbootpractice.domain.auth.dto.LoginRequest;
import com.example.springbootpractice.domain.auth.dto.LoginResponse;
import com.example.springbootpractice.domain.auth.dto.ReissueRequest;
import com.example.springbootpractice.domain.auth.service.AuthService;
import com.example.springbootpractice.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request.email(), request.password(), request.deviceId());
        return ApiResponse.success(response);
    }

    @PostMapping("/reissue")
    public ApiResponse<LoginResponse> reissue(@RequestBody ReissueRequest request) {
        LoginResponse response = authService.reissue(request.refreshToken());
        return ApiResponse.success(response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal Long userId) {
        authService.logout(userId);
        return ApiResponse.success(null);
    }
}
