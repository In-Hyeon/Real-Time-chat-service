package com.example.springbootpractice.domain.emoticon.controller;

import com.example.springbootpractice.domain.emoticon.dto.EmojiCreateRequest;
import com.example.springbootpractice.domain.emoticon.dto.EmojiResponse;
import com.example.springbootpractice.domain.emoticon.entity.Emoji;
import com.example.springbootpractice.domain.emoticon.service.EmojiService;
import com.example.springbootpractice.global.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/emojis")
@RequiredArgsConstructor
public class EmojiController {

    private final EmojiService emojiService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EmojiResponse> create(@RequestBody EmojiCreateRequest request) {
        Emoji emoji = emojiService.create(request.imageUrl(), request.category());
        return ApiResponse.success(EmojiResponse.from(emoji));
    }

    @GetMapping
    public ApiResponse<List<EmojiResponse>> findAll() {
        List<EmojiResponse> emojis = emojiService.findAll().stream().map(EmojiResponse::from).toList();
        return ApiResponse.success(emojis);
    }

    @PostMapping("/{id}/acquire")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<EmojiResponse> acquire(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        Emoji emoji = emojiService.acquire(userId, id).getEmoji();
        return ApiResponse.success(EmojiResponse.from(emoji));
    }

    @GetMapping("/mine")
    public ApiResponse<List<EmojiResponse>> findMine(@AuthenticationPrincipal Long userId) {
        List<EmojiResponse> emojis = emojiService.findMyEmojis(userId).stream()
                .map(ue -> EmojiResponse.from(ue.getEmoji()))
                .toList();
        return ApiResponse.success(emojis);
    }
}
