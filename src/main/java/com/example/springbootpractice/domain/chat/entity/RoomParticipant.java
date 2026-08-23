package com.example.springbootpractice.domain.chat.entity;

import com.example.springbootpractice.domain.user.entity.Profile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ROOM_PARTICIPANT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private ChatRoom room;

    // PROFILE은 (profile_id, user_id) 조합을 UNIQUE 키로 노출하고 있고,
    // ROOM_PARTICIPANT는 그 조합 전체를 복합 FK로 참조한다.
    // 즉 이 연관관계 하나가 DB 컬럼 두 개(profile_id, user_id)에 걸쳐 있다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "profile_id", referencedColumnName = "profile_id", nullable = false),
            @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    })
    private Profile profile;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    // DDL에 FK 제약이 걸려있지 않은 컬럼이라 연관관계 대신 순수 값으로 매핑
    @Column(name = "last_read_message_id", nullable = false)
    private Long lastReadMessageId;

    @Column(name = "custom_room_name", length = 100)
    private String customRoomName;

    @Column(name = "is_muted", nullable = false)
    private boolean isMuted = false;

    @Column(name = "is_pinned", nullable = false)
    private boolean isPinned = false;

    @Column(name = "background_image_url", length = 500)
    private String backgroundImageUrl;

    public static RoomParticipant create(ChatRoom room, Profile profile, Long lastReadMessageId) {
        RoomParticipant participant = new RoomParticipant();
        participant.room = room;
        participant.profile = profile;
        participant.lastReadMessageId = lastReadMessageId;
        participant.joinedAt = LocalDateTime.now();
        return participant;
    }

    public void updateSettings(String customRoomName, Boolean isMuted, Boolean isPinned, String backgroundImageUrl) {
        if (customRoomName != null) {
            this.customRoomName = customRoomName;
        }
        if (isMuted != null) {
            this.isMuted = isMuted;
        }
        if (isPinned != null) {
            this.isPinned = isPinned;
        }
        if (backgroundImageUrl != null) {
            this.backgroundImageUrl = backgroundImageUrl;
        }
    }
}
