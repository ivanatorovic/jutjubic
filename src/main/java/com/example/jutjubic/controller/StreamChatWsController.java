package com.example.jutjubic.controller;

import com.example.jutjubic.dto.StreamChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;

@Controller
public class StreamChatWsController {

    private final SimpMessagingTemplate template;

    public StreamChatWsController(SimpMessagingTemplate template) {
        this.template = template;
    }

    // Klijent šalje na: /app/stream/{videoId}/chat.send
    @MessageMapping("/stream/{videoId}/chat.send")
    public void sendToRoom(@DestinationVariable Long videoId, StreamChatMessage msg) {

        // Ne veruj klijentu 100%: setuj videoId i timestamp na serveru
        msg.setVideoId(videoId);
        msg.setTs(Instant.now());

        // Server objavljuje na: /topic/stream/{videoId}
        template.convertAndSend("/topic/stream/" + videoId, msg);
    }
}
