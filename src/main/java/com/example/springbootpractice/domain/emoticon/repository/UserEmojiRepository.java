package com.example.springbootpractice.domain.emoticon.repository;

import com.example.springbootpractice.domain.emoticon.entity.UserEmoji;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEmojiRepository extends JpaRepository<UserEmoji, Long> {

    boolean existsByUserIdAndEmojiId(Long userId, Long emojiId);

    List<UserEmoji> findAllByUserId(Long userId);
}
