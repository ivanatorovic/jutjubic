package com.example.jutjubic.service;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActiveUsers24hMetrics {


    private final Map<String, Long> lastSeenMs = new ConcurrentHashMap<>();
    private static final long TTL_MS = Duration.ofHours(24).toMillis();

    public ActiveUsers24hMetrics(MeterRegistry registry) {
        Gauge.builder("app_active_users_24h", lastSeenMs, m -> (double) m.size())
                .description("Number of distinct authenticated users active in last 24 hours")
                .register(registry);
    }

    public void markSeen(String email) {
        if (email == null || email.isBlank()) return;
        lastSeenMs.put(email, System.currentTimeMillis());
    }

    @Scheduled(fixedRate = 60_000)
    public void cleanup() {
        long now = System.currentTimeMillis();
        lastSeenMs.entrySet().removeIf(e -> now - e.getValue() > TTL_MS);
    }
}
