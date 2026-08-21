package com.example.springbootpractice.domain.friend.repository;

import com.example.springbootpractice.domain.friend.entity.Friend;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Long> {

    boolean existsByUserIdAndFriendId(Long userId, Long friendId);

    List<Friend> findAllByUserId(Long userId);
}
