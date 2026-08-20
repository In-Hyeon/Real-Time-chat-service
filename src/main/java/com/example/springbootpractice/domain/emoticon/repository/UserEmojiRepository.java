package com.example.springbootpractice.domain.emoticon.repository;

import com.example.springbootpractice.domain.emoticon.entity.UserEmoji;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserEmojiRepository extends JpaRepository<UserEmoji, Long> {
}
