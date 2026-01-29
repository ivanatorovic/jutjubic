package com.example.jutjubic.service;

import com.example.jutjubic.dto.CachedTrending;
import com.example.jutjubic.dto.VideoPublicDto;
import com.example.jutjubic.mapper.DtoMapper;
import com.example.jutjubic.model.Video;
import com.example.jutjubic.repository.CommentRepository;
import com.example.jutjubic.repository.VideoLikeRepository;
import com.example.jutjubic.repository.VideoRepository;
import com.example.jutjubic.util.GeoHash;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LocalTrendingCacheService {

    private final VideoRepository videoRepository;
    private final VideoLikeRepository likeRepository;
    private final CommentRepository commentRepository;

    public LocalTrendingCacheService(
            VideoRepository videoRepository,
            VideoLikeRepository likeRepository,
            CommentRepository commentRepository
    ) {
        this.videoRepository = videoRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
    }

    @Cacheable(
            cacheNames = "localTrending",
            key = "T(com.example.jutjubic.service.LocalTrendingService).cacheKey(#radiusKm, #lat, #lon)",
            sync = true
    )
    public CachedTrending getLocalTrendingCached(double radiusKm, double lat, double lon) {

        LocalDateTime computedAt = LocalDateTime.now();
        double r = radiusKm;

        System.out.println("[GEOHASH] radiusKm=" + r);

        int p = GeoHash.choosePrecisionForRadiusKm(lat, lon, r);
        System.out.println("[GEOHASH] chosen precision=" + p);

        List<String> prefixes = GeoHash.neighborPrefixes(lat, lon, p).stream().toList();
        System.out.println("[GEOHASH] prefixes size=" + prefixes.size());
        System.out.println("[GEOHASH] prefixes=" + prefixes);

        List<Video> candidates = videoRepository.findByGeohashPrefixes(
                prefixes.get(0), prefixes.get(1), prefixes.get(2),
                prefixes.get(3), prefixes.get(4), prefixes.get(5),
                prefixes.get(6), prefixes.get(7), prefixes.get(8),
                1000
        );

        if (candidates.isEmpty()) {
            return new CachedTrending(computedAt, List.of());
        }

        List<Video> localCandidates = candidates.stream()
                .filter(v -> v.getLatitude() != null && v.getLongitude() != null)
                .filter(v -> haversineKm(lat, lon, v.getLatitude(), v.getLongitude()) <= r)
                .toList();

        if (localCandidates.isEmpty()) {
            return new CachedTrending(computedAt, List.of());
        }

        List<Long> ids = localCandidates.stream().map(Video::getId).toList();

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

        List<VideoPublicDto> result = localCandidates.stream()
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

        return new CachedTrending(computedAt, result);
    }



    private double popularityScore(Video v,
                                   Map<Long, Long> likes,
                                   Map<Long, Long> comments,
                                   LocalDateTime now) {

        long views = Math.max(0, v.getViewCount());
        long likeCount = likes.getOrDefault(v.getId(), 0L);
        long commentCount = comments.getOrDefault(v.getId(), 0L);


        double popularity =
                1.4 * Math.log(views + 1.0)
                        + 1.8 * Math.log(likeCount + 1.0)
                        + 2.0 * Math.log(commentCount + 1.0);

        LocalDateTime created = v.getCreatedAt();
        if (created == null) return popularity;

        double ageHours = Duration.between(created, now).toMinutes() / 60.0;
        if (ageHours < 0) ageHours = 0;


        double graceHours = 7.0 * 24.0; // 168h


        double mildAt7Days = 1.25;
        double mildDecay;
        if (ageHours <= graceHours) {
            mildDecay = 1.0 + (mildAt7Days - 1.0) * (ageHours / graceHours);
            return popularity / mildDecay;
        }


        double extraHours = ageHours - graceHours;
        double halfLifeAfterGrace = 48.0;
        double strongDecay = Math.pow(2.0, extraHours / halfLifeAfterGrace);

        return popularity / (mildAt7Days * strongDecay);
    }


    private double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
