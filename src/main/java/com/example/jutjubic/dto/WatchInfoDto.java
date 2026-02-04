package com.example.jutjubic.dto;


import java.time.LocalDateTime;
public record WatchInfoDto(
        LocalDateTime serverNow,
        LocalDateTime streamStart,
        Integer durationSeconds,
        PremiereStatus status

) {}
