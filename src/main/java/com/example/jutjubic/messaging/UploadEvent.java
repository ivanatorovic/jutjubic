package com.example.jutjubic.messaging;

import java.time.LocalDateTime;

public record UploadEvent(
        Long videoId,
        String title,
        Long sizeMB,
        String authorUsername,
        String createdAt
) {}
