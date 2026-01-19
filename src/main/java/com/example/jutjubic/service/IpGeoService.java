package com.example.jutjubic.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class IpGeoService {

    private final RestClient restClient = RestClient.create("http://ip-api.com");

    public GeoPoint locate(String ip) {
        // localhost/loopback fallback (lokalni dev)
        if (ip == null || ip.isBlank()
                || "127.0.0.1".equals(ip)
                || "::1".equals(ip)
                || "0:0:0:0:0:0:0:1".equals(ip)) {
            return new GeoPoint(45.2671, 19.8335); // Novi Sad
        }

        try {
            IpApiResponse res = restClient.get()
                    .uri("/json/{ip}?fields=status,message,lat,lon,city,countryCode", ip)
                    .retrieve()
                    .body(IpApiResponse.class);

            if (res != null && "success".equalsIgnoreCase(res.status()) && res.lat() != null && res.lon() != null) {
                return new GeoPoint(res.lat(), res.lon());
            }

        } catch (Exception e) {
            // ignore, fallback below
        }

        // fallback ako servis ne radi / limit / greška
        return new GeoPoint(44.7866, 20.4489); // Beograd
    }

    public record GeoPoint(double lat, double lon) {}

    public record IpApiResponse(
            String status,
            String message,
            Double lat,
            Double lon,
            String city,
            String countryCode
    ) {}
}
