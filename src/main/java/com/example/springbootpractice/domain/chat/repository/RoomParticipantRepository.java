package com.example.springbootpractice.domain.chat.repository;

import com.example.springbootpractice.domain.chat.entity.RoomParticipant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {

    @Query("select rp from RoomParticipant rp where rp.profile.userId = :userId")
    List<RoomParticipant> findAllByProfileUserId(@Param("userId") Long userId);

    @Query("select rp from RoomParticipant rp where rp.room.id = :roomId and rp.profile.userId = :userId")
    Optional<RoomParticipant> findByRoomIdAndProfileUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);

    @Query("select rp from RoomParticipant rp where rp.room.id = :roomId")
    List<RoomParticipant> findAllByRoomId(@Param("roomId") Long roomId);
}
