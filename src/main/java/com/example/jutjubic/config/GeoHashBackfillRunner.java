package com.example.jutjubic.config;

import com.example.jutjubic.service.GeoHashBackfillService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeoHashBackfillRunner {

    @Bean
    CommandLineRunner geohashBackfill(GeoHashBackfillService svc) {
        return args -> {
            int updated = svc.backfillMissingGeohash();
            System.out.println("[GEOHASH] backfill updated=" + updated);
        };
    }
}
