package com.example.springbootpractice.domain.user.controller;

import com.example.springbootpractice.domain.user.dto.UserRegisterRequest;
import com.example.springbootpractice.domain.user.dto.UserResponse;
import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.service.UserService;
import com.example.springbootpractice.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> register(@RequestBody UserRegisterRequest request) {
        User user = userService.register(request.email(), request.password(), request.phoneNumber());
        return ApiResponse.success(UserResponse.from(user));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> findById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ApiResponse.success(UserResponse.from(user));
    }
}
