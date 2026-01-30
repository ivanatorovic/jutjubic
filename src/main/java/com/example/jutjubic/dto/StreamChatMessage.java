package com.example.jutjubic.dto;

import java.time.Instant;

public class StreamChatMessage {
    private Long videoId;
    private String sender;   // npr username
    private String content;
    private Instant ts;

    public StreamChatMessage() {}

    public StreamChatMessage(Long videoId, String sender, String content, Instant ts) {
        this.videoId = videoId;
        this.sender = sender;
        this.content = content;
        this.ts = ts;
    }

    public Long getVideoId() { return videoId; }
    public void setVideoId(Long videoId) { this.videoId = videoId; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Instant getTs() { return ts; }
    public void setTs(Instant ts) { this.ts = ts; }
}
