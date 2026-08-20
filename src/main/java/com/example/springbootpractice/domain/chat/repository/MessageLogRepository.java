package com.example.springbootpractice.domain.chat.repository;

import com.example.springbootpractice.domain.chat.entity.MessageLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageLogRepository extends JpaRepository<MessageLog, Long> {
}
