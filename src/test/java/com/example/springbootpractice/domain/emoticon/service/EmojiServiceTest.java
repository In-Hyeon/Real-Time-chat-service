package com.example.springbootpractice.domain.emoticon.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.springbootpractice.domain.emoticon.entity.Emoji;
import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.service.SubscriptionService;
import com.example.springbootpractice.domain.user.service.UserService;
import com.example.springbootpractice.global.exception.BusinessException;
import com.example.springbootpractice.global.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class EmojiServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private EmojiService emojiService;

    @Test
    void acquire_성공() {
        User user = userService.register("emoji-a@example.com", "password123", "01077770201");
        Emoji emoji = emojiService.create("http://example.com/a.png", "웃음");

        emojiService.acquire(user.getId(), emoji.getId());

        assertTrue(emojiService.canUse(user.getId(), emoji.getId()));
    }

    @Test
    void acquire_중복이면_예외() {
        User user = userService.register("emoji-b@example.com", "password123", "01077770202");
        Emoji emoji = emojiService.create("http://example.com/b.png", "슬픔");
        emojiService.acquire(user.getId(), emoji.getId());

        BusinessException e = assertThrows(BusinessException.class,
                () -> emojiService.acquire(user.getId(), emoji.getId()));
        assertEquals(ErrorCode.EMOJI_ALREADY_OWNED, e.getErrorCode());
    }

    @Test
    void canUse_구독중이면_구매안해도_true() {
        User user = userService.register("emoji-c@example.com", "password123", "01077770203");
        Emoji emoji = emojiService.create("http://example.com/c.png", "화남");
        subscriptionService.subscribe(user.getId(), 30);

        assertTrue(emojiService.canUse(user.getId(), emoji.getId()));
    }

    @Test
    void canUse_구매도구독도안했으면_false() {
        User user = userService.register("emoji-d@example.com", "password123", "01077770204");
        Emoji emoji = emojiService.create("http://example.com/d.png", "놀람");

        assertFalse(emojiService.canUse(user.getId(), emoji.getId()));
    }
}
