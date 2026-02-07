package com.example.jutjubic.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PopularBlockDto(LocalDateTime runAt, List<PopularVideoDto> top3) {}
