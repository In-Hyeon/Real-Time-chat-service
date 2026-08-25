package com.example.springbootpractice.domain.chat.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.springbootpractice.domain.auth.service.AuthService;
import com.example.springbootpractice.domain.chat.dto.MessageCreateRequest;
import com.example.springbootpractice.domain.chat.dto.MessageResponse;
import com.example.springbootpractice.domain.chat.entity.RoomParticipant;
import com.example.springbootpractice.domain.chat.service.ChatRoomService;
import com.example.springbootpractice.domain.user.entity.Profile;
import com.example.springbootpractice.domain.user.entity.User;
import com.example.springbootpractice.domain.user.service.ProfileService;
import com.example.springbootpractice.domain.user.service.UserService;
import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

/**
 * REST(MessageService.send)로 이미 검증한 저장 로직은 그대로 재사용하고,
 * 여기서는 "A가 SEND하면 B가 SUBSCRIBE로 실시간으로 받는가"만 별도로 검증한다.
 * @Transactional을 쓰지 않는다 — 실제 내장 서버(RANDOM_PORT)가 뜨고, STOMP 메시지 처리는
 * 별도 스레드의 독립 트랜잭션에서 실행되기 때문에 테스트 메서드에 트랜잭션을 걸어도 롤백되지 않는다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageSocketControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private AuthService authService;

    @Test
    void 실시간_메시지_수신_성공() throws Exception {
        String suffix = String.valueOf(System.currentTimeMillis());

        User me = userService.register("ws-a-" + suffix + "@example.com", "password123", "010" + last8(suffix));
        User other = userService.register("ws-b-" + suffix + "@example.com", "password123", "011" + last8(suffix));
        Profile myProfile = profileService.create(me.getId(), "wsA", "DEFAULT");
        Profile otherProfile = profileService.create(other.getId(), "wsB", "DEFAULT");

        RoomParticipant myParticipant = chatRoomService.create(
                me.getId(), "웹소켓테스트방", "GROUP", myProfile.getProfileId(), java.util.List.of(otherProfile.getProfileId()));
        Long roomId = myParticipant.getRoom().getId();

        String meAccessToken =
                authService.login("ws-a-" + suffix + "@example.com", "password123", "device-1").accessToken();
        String otherAccessToken =
                authService.login("ws-b-" + suffix + "@example.com", "password123", "device-1").accessToken();

        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new JacksonJsonMessageConverter());

        // B: 구독만 하는 쪽
        StompHeaders bConnectHeaders = new StompHeaders();
        bConnectHeaders.add("Authorization", "Bearer " + otherAccessToken);
        StompSession bSession = stompClient
                .connectAsync("ws://localhost:" + port + "/ws", new WebSocketHttpHeaders(), bConnectHeaders,
                        new StompSessionHandlerAdapter() {
                        })
                .get(5, TimeUnit.SECONDS);

        BlockingQueue<MessageResponse> received = new LinkedBlockingQueue<>();
        bSession.subscribe("/topic/rooms/" + roomId, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return MessageResponse.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                received.add((MessageResponse) payload);
            }
        });

        Thread.sleep(300);

        // A: 실제로 메시지를 보내는 쪽 (B와는 별개의 세션)
        StompHeaders aConnectHeaders = new StompHeaders();
        aConnectHeaders.add("Authorization", "Bearer " + meAccessToken);
        StompSession aSession = stompClient
                .connectAsync("ws://localhost:" + port + "/ws", new WebSocketHttpHeaders(), aConnectHeaders,
                        new StompSessionHandlerAdapter() {
                        })
                .get(5, TimeUnit.SECONDS);

        MessageCreateRequest sendRequest =
                new MessageCreateRequest("TEXT", "실시간 테스트 메시지", null, null, null, null, null, null);
        StompHeaders sendHeaders = new StompHeaders();
        sendHeaders.setDestination("/app/rooms/" + roomId + "/messages");
        aSession.send(sendHeaders, sendRequest);

        MessageResponse response = received.poll(5, TimeUnit.SECONDS);

        assertNotNull(response);
        assertEquals("실시간 테스트 메시지", response.content());
        assertEquals(me.getId(), response.senderId());

        aSession.disconnect();
        bSession.disconnect();
    }

    private String last8(String s) {
        return s.length() >= 8 ? s.substring(s.length() - 8) : s;
    }
}
