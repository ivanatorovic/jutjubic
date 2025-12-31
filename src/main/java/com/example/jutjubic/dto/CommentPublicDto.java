package com.example.jutjubic.dto;

import java.time.LocalDateTime;

public record CommentPublicDto(
        Long id,
        String text,
        Long userId,
        String username,
        LocalDateTime createdAt
) {}
