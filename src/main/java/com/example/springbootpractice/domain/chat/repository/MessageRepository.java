package com.example.springbootpractice.domain.chat.repository;

import com.example.springbootpractice.domain.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
