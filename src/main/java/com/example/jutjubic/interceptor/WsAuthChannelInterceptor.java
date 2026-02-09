package com.example.jutjubic.interceptor;

import com.example.jutjubic.repository.UserRepository;
import com.example.jutjubic.util.TokenUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WsAuthChannelInterceptor implements ChannelInterceptor {

    private final TokenUtils tokenUtils;
    private final UserRepository userRepository;

    private final Map<String, Authentication> sessions = new ConcurrentHashMap<>();

    public WsAuthChannelInterceptor(TokenUtils tokenUtils, UserRepository userRepository) {
        this.tokenUtils = tokenUtils;
        this.userRepository = userRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);

        String sessionId = acc.getSessionId();
        StompCommand cmd = acc.getCommand();


        if (cmd == StompCommand.CONNECT) {
            String authH = acc.getFirstNativeHeader("Authorization");
            if (authH == null) authH = acc.getFirstNativeHeader("authorization");

            if (authH != null && authH.startsWith("Bearer ")) {
                String token = authH.substring(7);
                String email = tokenUtils.getEmailFromTokenSafe(token);

                System.out.println("[WS CONNECT] email=" + email);

                if (email != null && !email.isBlank()) {
                    userRepository.findByEmail(email).orElseThrow();

                    Authentication a = new UsernamePasswordAuthenticationToken(
                            email, null, List.of(() -> "ROLE_USER")
                    );

                    sessions.put(sessionId, a);

                    acc.setUser(a);
                    acc.setHeader(SimpMessageHeaderAccessor.USER_HEADER, a);

                    return MessageBuilder.createMessage(message.getPayload(), acc.getMessageHeaders());
                }
            }
        }

        if (acc.getUser() == null && sessionId != null) {
            Authentication a = sessions.get(sessionId);
            if (a != null) {
                acc.setUser(a);
                acc.setHeader(SimpMessageHeaderAccessor.USER_HEADER, a);
                return MessageBuilder.createMessage(message.getPayload(), acc.getMessageHeaders());
            }
        }

        if (cmd == StompCommand.DISCONNECT && sessionId != null) {
            sessions.remove(sessionId);
        }

        return message;
    }
}
