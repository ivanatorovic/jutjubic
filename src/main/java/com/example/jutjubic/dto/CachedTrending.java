package com.example.jutjubic.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CachedTrending(LocalDateTime computedAt, List<VideoPublicDto> items) {}
