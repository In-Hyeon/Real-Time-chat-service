package com.example.springbootpractice.domain.emoticon.repository;

import com.example.springbootpractice.domain.emoticon.entity.Emoji;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmojiRepository extends JpaRepository<Emoji, Long> {
}
