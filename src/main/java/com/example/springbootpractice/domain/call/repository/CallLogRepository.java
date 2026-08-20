package com.example.springbootpractice.domain.call.repository;

import com.example.springbootpractice.domain.call.entity.CallLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CallLogRepository extends JpaRepository<CallLog, Long> {
}
