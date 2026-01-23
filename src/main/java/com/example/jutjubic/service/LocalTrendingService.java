package com.example.jutjubic.service;

import com.example.jutjubic.dto.VideoPublicDto;
import com.example.jutjubic.mapper.DtoMapper;
import com.example.jutjubic.model.Video;
import com.example.jutjubic.repository.CommentRepository;
import com.example.jutjubic.repository.VideoLikeRepository;
import com.example.jutjubic.repository.VideoRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import com.example.jutjubic.util.GeoHash;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LocalTrendingService {

    private final VideoRepository videoRepository;
    private final VideoLikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final IpGeoService ipGeoService;

    public LocalTrendingService(
            VideoRepository videoRepository,
            VideoLikeRepository likeRepository,
            CommentRepository commentRepository,
            IpGeoService ipGeoService
    ) {
        this.videoRepository = videoRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.ipGeoService = ipGeoService;
    }

    public List<VideoPublicDto> getLocalTrending(
            double radiusKm,
            Double lat,
            Double lon,
            HttpServletRequest request
    ) {
        // 1️⃣ Odredi lokaciju korisnika (S2 - ostaje)
        double userLat;
        double userLon;

        if (lat != null && lon != null) {
            userLat = lat;
            userLon = lon;
            System.out.println("[LOCAL] using GPS lat=" + userLat + " lon=" + userLon);
        } else {
            String ip = extractClientIp(request);
            IpGeoService.GeoPoint p = ipGeoService.locate(ip);
            userLat = p.lat();
            userLon = p.lon();
            System.out.println("[LOCAL] using IP fallback ip=" + ip + " -> lat=" + p.lat() + " lon=" + p.lon());
        }

        double r = radiusKm;

        System.out.println("[GEOHASH] radiusKm=" + r);

        int p = GeoHash.choosePrecisionForRadiusKm(userLat, userLon, r);
        System.out.println("[GEOHASH] chosen precision=" + p);

        List<String> prefixes = GeoHash.neighborPrefixes(userLat, userLon, p)
                .stream()
                .toList();

        System.out.println("[GEOHASH] prefixes size=" + prefixes.size());
        System.out.println("[GEOHASH] prefixes=" + prefixes);


        List<Video> candidates = videoRepository.findByGeohashPrefixes(
                prefixes.get(0), prefixes.get(1), prefixes.get(2),
                prefixes.get(3), prefixes.get(4), prefixes.get(5),
                prefixes.get(6), prefixes.get(7), prefixes.get(8),
                1000
        );

        if (candidates.isEmpty()) return List.of();

// 3️⃣ Precizno: krug (Haversine) nad kandidatima
        List<Video> localCandidates = candidates.stream()
                .filter(v -> v.getLatitude() != null && v.getLongitude() != null)
                .filter(v -> haversineKm(userLat, userLon, v.getLatitude(), v.getLongitude()) <= r)
                .toList();


        if (localCandidates.isEmpty()) return List.of();

        List<Long> ids = localCandidates.stream().map(Video::getId).toList();

        // 4️⃣ Batch counts (bez N+1)
        Map<Long, Long> likeMap = likeRepository.countLikesByVideoIds(ids).stream()
                .collect(Collectors.toMap(
                        VideoLikeRepository.IdCount::getVideoId,
                        VideoLikeRepository.IdCount::getCnt
                ));

        Map<Long, Long> commentMap = commentRepository.countCommentsByVideoIds(ids).stream()
                .collect(Collectors.toMap(
                        CommentRepository.IdCount::getVideoId,
                        CommentRepository.IdCount::getCnt
                ));

        LocalDateTime now = LocalDateTime.now();

        return localCandidates.stream()
                .map(v -> Map.entry(v, popularityScore(v, likeMap, commentMap, now)))
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .limit(20)
                .map(e -> {
                    Video v = e.getKey();
                    long likeCount = likeMap.getOrDefault(v.getId(), 0L);
                    long commentCount = commentMap.getOrDefault(v.getId(), 0L);
                    return DtoMapper.toVideoPublicDto(v, likeCount, commentCount);
                })
                .toList();

    }

    // ---------------- S1 SCORE ----------------
    private double popularityScore(Video v,
                                   Map<Long, Long> likes,
                                   Map<Long, Long> comments,
                                   LocalDateTime now) {

        long viewCount = v.getViewCount();
        long likeCount = likes.getOrDefault(v.getId(), 0L);
        long commentCount = comments.getOrDefault(v.getId(), 0L);

        // baza: više parametara (S1)
        double base = Math.log(viewCount + 1.0)
                + 3.0 * likeCount
                + 5.0 * commentCount;

        // recency/time-decay (imaš createdAt u Video)
        LocalDateTime created = v.getCreatedAt();
        if (created == null) return base;

        double ageHours = Duration.between(created, now).toMinutes() / 60.0;
        double decay = 1.0 + (ageHours / 24.0); // posle 24h ~ duplo slabije

        return base / decay;
    }

    // ---------------- HAVERSINE ----------------
    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // ================== HELPERS ==================
    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
