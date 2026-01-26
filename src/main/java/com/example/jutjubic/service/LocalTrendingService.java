package com.example.jutjubic.service;

import com.example.jutjubic.dto.CachedTrending;
import com.example.jutjubic.dto.VideoPublicDto;
import com.example.jutjubic.logger.LocalTrendingCacheStats;
import com.example.jutjubic.mapper.DtoMapper;
import com.example.jutjubic.model.Video;
import com.example.jutjubic.repository.CommentRepository;
import com.example.jutjubic.repository.VideoLikeRepository;
import com.example.jutjubic.repository.VideoRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.example.jutjubic.util.GeoHash;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
 // ime keša
public class LocalTrendingService {

    private final VideoRepository videoRepository;
    private final VideoLikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final IpGeoService ipGeoService;
    private final CacheManager cacheManager;
    private final LocalTrendingCacheStats cacheStats;

    private final LocalTrendingCacheService cacheService;
    public LocalTrendingService(
            VideoRepository videoRepository,
            VideoLikeRepository likeRepository,
            CommentRepository commentRepository,
            IpGeoService ipGeoService,
            CacheManager cacheManager,
            LocalTrendingCacheService cacheService,
            LocalTrendingCacheStats cacheStats

    ) {
        this.videoRepository = videoRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.ipGeoService = ipGeoService;
        this.cacheManager = cacheManager;
        this.cacheService = cacheService;
        this.cacheStats = cacheStats;

    }

    public List<VideoPublicDto> getLocalTrending(
            double radiusKm,
            Double lat,
            Double lon,
            HttpServletRequest request
    ) {
        // 1) Odredi lokaciju (isto kao kod tebe)
        double userLat;
        double userLon;

        if (lat != null && lon != null) {
            userLat = lat;
            userLon = lon;
            System.out.printf(
                    "[LOCAL] SOURCE=GPS lat=%.6f lon=%.6f radiusKm=%.1f%n",
                    userLat, userLon, radiusKm
            );
        } else {
            String ip = extractClientIp(request);
            IpGeoService.GeoPoint p = ipGeoService.locate(ip);
            userLat = p.lat();
            userLon = p.lon();
            System.out.printf(
                    "[LOCAL] SOURCE=IP_FALLBACK ip=%s lat=%.6f lon=%.6f radiusKm=%.1f%n",
                    ip, userLat, userLon, radiusKm
            );
        }
        String key = cacheKey(radiusKm, userLat, userLon);
        Cache cache = cacheManager.getCache("localTrending");
        Object cached = (cache != null) ? cache.get(key, Object.class) : null;

        boolean hit = (cached != null);
        if (hit) cacheStats.hit();
        else cacheStats.miss();




        CachedTrending ct = cacheService.getLocalTrendingCached(radiusKm, userLat, userLon);

        long stalenessSec = Duration.between(ct.computedAt(), LocalDateTime.now()).getSeconds();
        System.out.println("[FRESHNESS] staleness=" + stalenessSec + "s key=" + key);

        return ct.items();

    }



    // ================== HELPERS ==================
    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    public static String cacheKey(double radiusKm, double lat, double lon) {
        // fiksiramo preciznost da ne bude milion ključeva
        // 6 je ok start (cell par km do ~10km zavisi od lokacije)
        int precision = 6;

        String cell = GeoHash.encode(lat, lon, precision);
        // radius u ključ da se razlikuju 5km i 20km trendinzi
        return String.format("%.1f:%s", radiusKm, cell);
    }

}

