package com.example.jutjubic.dto;

import java.time.LocalDateTime;
import java.util.List;

public record VideoPublicDto(
        Long id,
        String title,
        String description,
        List<String> tags,
        Long sizeMB,
        LocalDateTime createdAt,
        String location,
        Long userId,
        String username
) {}
