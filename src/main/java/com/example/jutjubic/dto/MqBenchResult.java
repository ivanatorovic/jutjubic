package com.example.jutjubic.dto;

public record MqBenchResult(
        int count,
        double avgJsonSerializeMicros,
        double avgPbSerializeMicros,
        double avgJsonBytes,
        double avgPbBytes
) {}
