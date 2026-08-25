package com.example.springbootpractice.global.websocket;

import com.example.springbootpractice.domain.chat.repository.RoomParticipantRepository;
import com.example.springbootpractice.global.jwt.JwtTokenProvider;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * REST의 JwtAuthenticationFilter는 서블릿 필터라 WebSocket 핸드셰이크 이후의 STOMP 프레임에는 적용되지 않는다.
 * 그래서 STOMP CONNECT 프레임 헤더에 실려 온 토큰을 여기서 직접 검증하고, 세션에 Principal을 붙여준다.
 * 이후 SUBSCRIBE/SEND 프레임에는 Spring이 같은 세션의 Principal을 자동으로 다시 붙여주므로 재검증할 필요는 없다.
 */
@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final RoomParticipantRepository roomParticipantRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // wrap()은 매번 새 래퍼를 만들어 setUser() 등의 변경이 원본 메시지에 반영되지 않는다.
        // getAccessor()는 메시지에 내장된 "그 accessor 원본"을 그대로 돌려주므로, 여기서 바꾼 값이
        // 메시지를 다시 만들지 않아도 그대로 유지된다.
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Long userId = authenticate(accessor.getFirstNativeHeader(AUTH_HEADER));
            accessor.setUser(new StompPrincipal(String.valueOf(userId)));
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            Long userId = currentUserId(accessor.getUser());
            Long roomId = extractRoomId(accessor.getDestination());
            if (roomId != null && roomParticipantRepository.findByRoomIdAndProfileUserId(roomId, userId).isEmpty()) {
                throw new MessagingException("참여하지 않은 채팅방입니다.");
            }
        }

        return message;
    }

    private Long authenticate(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new MessagingException("인증이 필요합니다.");
        }
        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        if (!jwtTokenProvider.isValid(token)) {
            throw new MessagingException("유효하지 않거나 만료된 토큰입니다.");
        }
        return jwtTokenProvider.getUserId(token);
    }

    private Long currentUserId(Principal principal) {
        if (principal == null) {
            throw new MessagingException("인증이 필요합니다.");
        }
        return Long.valueOf(principal.getName());
    }

    private Long extractRoomId(String destination) {
        if (destination == null) {
            return null;
        }
        String[] parts = destination.split("/");
        if (parts.length >= 4 && "topic".equals(parts[1]) && "rooms".equals(parts[2])) {
            try {
                return Long.valueOf(parts[3]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
