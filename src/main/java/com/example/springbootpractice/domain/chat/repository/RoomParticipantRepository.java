package com.example.springbootpractice.domain.chat.repository;

import com.example.springbootpractice.domain.chat.entity.RoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {
}
