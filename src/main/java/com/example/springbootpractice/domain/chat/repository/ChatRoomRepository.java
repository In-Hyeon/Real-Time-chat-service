package com.example.springbootpractice.domain.chat.repository;

import com.example.springbootpractice.domain.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
