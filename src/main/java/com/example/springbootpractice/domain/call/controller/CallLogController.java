package com.example.springbootpractice.domain.call.controller;

import com.example.springbootpractice.domain.call.dto.CallLogCreateRequest;
import com.example.springbootpractice.domain.call.dto.CallLogResponse;
import com.example.springbootpractice.domain.call.entity.CallLog;
import com.example.springbootpractice.domain.call.service.CallLogService;
import com.example.springbootpractice.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calls")
@RequiredArgsConstructor
public class CallLogController {

    private final CallLogService callLogService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CallLogResponse> create(@AuthenticationPrincipal Long userId,
                                                @RequestBody CallLogCreateRequest request) {
        CallLog callLog = callLogService.create(userId, request.roomId(), request.receiverUserId(),
                request.callType(), request.callStatus(), request.durationSeconds());
        return ApiResponse.success(CallLogResponse.from(callLog));
    }

    @GetMapping
    public ApiResponse<List<CallLogResponse>> myCallLogs(@AuthenticationPrincipal Long userId) {
        List<CallLogResponse> logs = callLogService.findMyCallLogs(userId).stream()
                .map(CallLogResponse::from)
                .toList();
        return ApiResponse.success(logs);
    }
}
