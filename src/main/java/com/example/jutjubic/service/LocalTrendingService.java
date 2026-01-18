package com.example.jutjubic.service;

import com.example.jutjubic.dto.VideoPublicDto;
import com.example.jutjubic.mapper.DtoMapper;
import com.example.jutjubic.model.Video;
import com.example.jutjubic.repository.CommentRepository;
import com.example.jutjubic.repository.VideoLikeRepository;
import com.example.jutjubic.repository.VideoRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

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
        // 1️⃣ Odredi lokaciju korisnika
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



        // 2️⃣ Filtriraj videe po radijusu
        return videoRepository.findAll()
                .stream()
                .limit(5)
                .map(v -> {
                    long likeCount = likeRepository.countByVideoId(v.getId());
                    long commentCount = commentRepository.countByVideoId(v.getId());
                    return DtoMapper.toVideoPublicDto(v, likeCount, commentCount);
                })
                .toList();
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
