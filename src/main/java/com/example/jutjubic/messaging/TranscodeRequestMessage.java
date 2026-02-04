package com.example.jutjubic.messaging;

import java.util.UUID;

public record TranscodeRequestMessage(
        UUID jobId,
        Long videoId,
        String inputPath
) {}
