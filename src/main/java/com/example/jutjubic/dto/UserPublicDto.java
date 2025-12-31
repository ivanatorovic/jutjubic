package com.example.jutjubic.dto;

import java.time.LocalDateTime;

public record UserPublicDto(
        Long id,
        String username,
        String firstName,
        String lastName,
        String address,
        LocalDateTime createdAt
) {}
