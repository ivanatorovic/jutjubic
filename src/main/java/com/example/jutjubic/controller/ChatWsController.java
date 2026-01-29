// src/main/java/.../controller/ChatWsController.java
package com.example.jutjubic.controller;

import com.example.jutjubic.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

import java.time.OffsetDateTime;

@Controller
public class ChatWsController {

    // Klijent šalje poruke na: /app/video/{videoId}/chat
    // Server broadcastuje na:   /topic/video/{videoId}
    @MessageMapping("/video/{videoId}/chat")
    @SendTo("/topic/video/{videoId}")
    public ChatMessage chat(@DestinationVariable Long videoId,
                            ChatMessage msg) {

        // zaštite da ne dođe null
        if (msg == null) msg = new ChatMessage();

        msg.setVideoId(videoId);
        msg.setType(msg.getType() == null ? "CHAT" : msg.getType());
        msg.setTimestamp(OffsetDateTime.now().toString());

        // NEMA baze, NEMA istorije -> samo prosledi
        return msg;
    }

    // (opciono) join event, ako želiš "Ivana joined"
    @MessageMapping("/video/{videoId}/join")
    @SendTo("/topic/video/{videoId}")
    public ChatMessage join(@DestinationVariable Long videoId,
                            ChatMessage msg) {
        if (msg == null) msg = new ChatMessage();
        msg.setVideoId(videoId);
        msg.setType("JOIN");
        msg.setTimestamp(OffsetDateTime.now().toString());
        return msg;
    }
}
