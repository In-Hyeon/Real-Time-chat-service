package com.example.springbootpractice.domain.call.repository;

import com.example.springbootpractice.domain.call.entity.CallLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CallLogRepository extends JpaRepository<CallLog, Long> {

    @Query("select c from CallLog c where c.caller.id = :userId or c.receiver.id = :userId order by c.createdAt desc")
    List<CallLog> findAllByUserId(@Param("userId") Long userId);
}
