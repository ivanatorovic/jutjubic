package com.example.jutjubic.service;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IpRateLimiterService {

    private final RateLimiterRegistry registry;
    private final ConcurrentHashMap<String, RateLimiter> limiters = new ConcurrentHashMap<>();

    public IpRateLimiterService() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(5)                         // 5 pokušaja
                .limitRefreshPeriod(Duration.ofMinutes(1))  // po minuti
                .timeoutDuration(Duration.ZERO)             // ne čekaj, odmah odbij
                .build();

        this.registry = RateLimiterRegistry.of(config);
    }

    public boolean allow(String ip) {
        RateLimiter limiter = limiters.computeIfAbsent(
                ip,
                key -> registry.rateLimiter("login-" + key)
        );
        return limiter.acquirePermission(); // true -> pusti, false -> 429
    }
}
