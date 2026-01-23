package com.example.jutjubic.service;

import com.example.jutjubic.repository.VideoRepository;
import com.example.jutjubic.util.GeoHash;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GeoHashBackfillService {

    private final VideoRepository videoRepository;

    public GeoHashBackfillService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }


    public int backfillMissingGeohash() {
        var videos = videoRepository.findAll();
        int updated = 0;

        for (var v : videos) {
            if (v.getLatitude() == null || v.getLongitude() == null) continue;
            if (v.getGeohash() != null && !v.getGeohash().isBlank()) continue;

            String gh = GeoHash.encode(v.getLatitude(), v.getLongitude(), 8);
            v.setGeohash(gh);
            updated++;
        }

        videoRepository.saveAll(videos);
        return updated;
    }
}
