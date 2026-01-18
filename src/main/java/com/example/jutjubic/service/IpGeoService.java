package com.example.jutjubic.service;

import org.springframework.stereotype.Service;

@Service
public class IpGeoService {

    public GeoPoint locate(String ip) {
        // localhost fallback
        if ("127.0.0.1".equals(ip) || "::1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            return new GeoPoint(45.2671, 19.8335); // Novi Sad
        }

        // default fallback (npr. Beograd)
        return new GeoPoint(44.7866, 20.4489);
    }

    public record GeoPoint(double lat, double lon) {}
}
