// src/main/java/.../config/WebSocketConfig.java
package com.example.jutjubic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // SockJS olakšava rad u browseru i preko proxy-a
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // klijent šalje na /app/...
        registry.setApplicationDestinationPrefixes("/app");
        // klijent se pretplaćuje na /topic/...
        registry.enableSimpleBroker("/topic");
    }
}
