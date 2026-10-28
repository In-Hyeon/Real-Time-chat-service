package com.example.springbootpractice.domain.chat.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.springbootpractice.domain.chat.entity.RoomParticipant;
import com.example.springbootpractice.domain.user.entity.Profile;
import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.service.ProfileService;
import com.example.springbootpractice.domain.user.service.UserService;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * 메시지 목록 조회 시 나가는 쿼리 수가 메시지 개수와 상관없이 일정한지 확인한다.
 * 타입별 내용을 메시지마다 따로 조회하면(N+1) 개수에 비례해 늘어난다.
 */
@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@Transactional
class MessageQueryCountTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findMessages_쿼리수는_메시지_개수와_무관() {
        User user = userService.register("qc-query-count@example.com", "password123", "01055501234");
        Profile profile = profileService.create(user.getId(), "측정", "DEFAULT");
        Long userId = user.getId();
        Long profileId = profile.getProfileId();
        entityManager.flush();
        entityManager.clear();

        RoomParticipant participant = chatRoomService.create(userId, "측정방", "GROUP", profileId, List.of());
        Long roomId = participant.getRoom().getId();
        entityManager.flush();
        entityManager.clear();

        for (int i = 0; i < 100; i++) {
            if (i % 2 == 0) {
                messageService.send(userId, roomId, "TEXT", "메시지" + i, null, null, null, null, null, null);
            } else {
                messageService.send(userId, roomId, "FILE", null, "https://example.com/f" + i, "f" + i + ".txt", 10L,
                        null, null, null);
            }
        }
        entityManager.flush();
        entityManager.clear();

        long q30 = countQueries(() -> messageService.findMessages(userId, roomId, null, 30));
        long q100 = countQueries(() -> messageService.findMessages(userId, roomId, null, 100));
        System.out.println("[QUERY COUNT] 30 messages = " + q30 + ", 100 messages = " + q100);

        assertEquals(q30, q100);
    }

    private long countQueries(Runnable action) {
        Statistics stats = entityManager.getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
        entityManager.clear();
        stats.clear();
        action.run();
        return stats.getPrepareStatementCount();
    }
}
