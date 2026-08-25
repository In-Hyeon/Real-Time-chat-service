package com.example.springbootpractice.domain.emoticon.service;

import com.example.springbootpractice.domain.emoticon.entity.Emoji;
import com.example.springbootpractice.domain.emoticon.entity.UserEmoji;
import com.example.springbootpractice.domain.emoticon.repository.EmojiRepository;
import com.example.springbootpractice.domain.emoticon.repository.UserEmojiRepository;
import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.entity.UserSubscription;
import com.example.springbootpractice.domain.user.repository.UserRepository;
import com.example.springbootpractice.domain.user.repository.UserSubscriptionRepository;
import com.example.springbootpractice.global.exception.BusinessException;
import com.example.springbootpractice.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * canUse()는 MessageService가 EMOJI 타입 메시지를 보낼 때 재사용한다
 * (domain_spec.md: 구독 활성 상태면 개별 구매 여부와 무관하게 카탈로그 전체 사용 가능).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmojiService {

    private final EmojiRepository emojiRepository;
    private final UserEmojiRepository userEmojiRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserRepository userRepository;

    @Transactional
    public Emoji create(String imageUrl, String category) {
        return emojiRepository.save(Emoji.create(imageUrl, category));
    }

    public List<Emoji> findAll() {
        return emojiRepository.findAll();
    }

    public Emoji findById(Long emojiId) {
        return emojiRepository.findById(emojiId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMOJI_NOT_FOUND));
    }

    @Transactional
    public UserEmoji acquire(Long userId, Long emojiId) {
        Emoji emoji = findById(emojiId);

        if (userEmojiRepository.existsByUserIdAndEmojiId(userId, emojiId)) {
            throw new BusinessException(ErrorCode.EMOJI_ALREADY_OWNED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return userEmojiRepository.save(UserEmoji.create(user, emoji));
    }

    public List<UserEmoji> findMyEmojis(Long userId) {
        return userEmojiRepository.findAllByUserId(userId);
    }

    public boolean canUse(Long userId, Long emojiId) {
        if (userEmojiRepository.existsByUserIdAndEmojiId(userId, emojiId)) {
            return true;
        }
        return userSubscriptionRepository.findByUserId(userId)
                .map(UserSubscription::isCurrentlyActive)
                .orElse(false);
    }
}
