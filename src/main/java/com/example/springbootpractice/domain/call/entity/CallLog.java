package com.example.springbootpractice.domain.call.entity;

import com.example.springbootpractice.domain.chat.entity.ChatRoom;
import com.example.springbootpractice.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CALL_LOG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caller_id", nullable = false)
    private User caller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(name = "call_type", nullable = false, length = 20)
    private String callType;

    @Column(name = "call_status", nullable = false, length = 20)
    private String callStatus;

    @Column(name = "duration_seconds", nullable = false)
    private int durationSeconds;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static CallLog create(ChatRoom room, User caller, User receiver, String callType, String callStatus, int durationSeconds) {
        CallLog callLog = new CallLog();
        callLog.room = room;
        callLog.caller = caller;
        callLog.receiver = receiver;
        callLog.callType = callType;
        callLog.callStatus = callStatus;
        callLog.durationSeconds = durationSeconds;
        callLog.createdAt = LocalDateTime.now();
        return callLog;
    }
}
