// src/main/java/.../dto/ChatMessage.java
package com.example.jutjubic.dto;

public class ChatMessage {
    private String type;      // "CHAT" | "JOIN" | "LEAVE" (opciono)
    private Long videoId;
    private Long senderId;    // opciono
    private String sender;    // username
    private String text;
    private String timestamp; // ISO string

    public ChatMessage() {}

    // getters/setters

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getVideoId() { return videoId; }
    public void setVideoId(Long videoId) { this.videoId = videoId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
