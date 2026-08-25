package com.example.springbootpractice.domain.chat.controller;

import com.example.springbootpractice.domain.chat.dto.MessageCreateRequest;
import com.example.springbootpractice.domain.chat.dto.MessageResponse;
import com.example.springbootpractice.domain.chat.service.MessageService;
import com.example.springbootpractice.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms/{roomId}/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MessageResponse> send(@AuthenticationPrincipal Long userId,
                                              @PathVariable Long roomId,
                                              @RequestBody MessageCreateRequest request) {
        MessageResponse response = messageService.send(userId, roomId, request.messageType(), request.content(),
                request.fileUrl(), request.originalName(), request.fileSize(), request.parentMessageId(),
                request.emojiId(), request.voucherId());
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<List<MessageResponse>> findMessages(@AuthenticationPrincipal Long userId,
                                                             @PathVariable Long roomId,
                                                             @RequestParam(required = false) Long before,
                                                             @RequestParam(required = false, defaultValue = "30") int size) {
        List<MessageResponse> messages = messageService.findMessages(userId, roomId, before, size);
        return ApiResponse.success(messages);
    }

    @DeleteMapping("/{messageId}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal Long userId,
                                     @PathVariable Long roomId,
                                     @PathVariable Long messageId) {
        messageService.delete(userId, roomId, messageId);
        return ApiResponse.success(null);
    }
}
