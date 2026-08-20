package com.example.springbootpractice.domain.friend.repository;

import com.example.springbootpractice.domain.friend.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {
}
