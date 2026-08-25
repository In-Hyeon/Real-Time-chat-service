package com.example.springbootpractice.domain.chat.repository;

import com.example.springbootpractice.domain.chat.entity.Message;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("select m from Message m where m.room.id = :roomId and (:before is null or m.id < :before) order by m.id desc")
    List<Message> findPage(@Param("roomId") Long roomId, @Param("before") Long before, Pageable pageable);

    long countByRoomIdAndIdGreaterThan(Long roomId, Long lastReadMessageId);
}
