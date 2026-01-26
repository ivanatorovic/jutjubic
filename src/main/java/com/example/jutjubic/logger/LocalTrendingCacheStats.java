package com.example.jutjubic.logger;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class LocalTrendingCacheStats {

    private final AtomicLong hits = new AtomicLong();
    private final AtomicLong misses = new AtomicLong();

    public void hit() {
        hits.incrementAndGet();
    }

    public void miss() {
        misses.incrementAndGet();
    }

    // 👇 OVO SE POZIVA KAD SE APLIKACIJA GASI
    @PreDestroy
    public void printFinalStats() {
        System.out.println("======================================");
        System.out.println("[CACHE FINAL STATS] localTrending");
        System.out.println("HITS   = " + hits.get());
        System.out.println("MISSES = " + misses.get());
        System.out.println("======================================");
    }
}
