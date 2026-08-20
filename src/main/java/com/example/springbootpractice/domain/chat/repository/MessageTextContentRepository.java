package com.example.springbootpractice.domain.chat.repository;

import com.example.springbootpractice.domain.chat.entity.MessageTextContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageTextContentRepository extends JpaRepository<MessageTextContent, Long> {
}
