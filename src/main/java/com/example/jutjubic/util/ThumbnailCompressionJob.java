package com.example.jutjubic.util;

import com.example.jutjubic.model.Video;
import com.example.jutjubic.repository.VideoRepository;
import com.example.jutjubic.service.ThumbnailCompressionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class ThumbnailCompressionJob {

    private final VideoRepository videoRepository;
    private final ThumbnailCompressionService compressor;
    private final CacheManager cacheManager;

    public ThumbnailCompressionJob(VideoRepository videoRepository,
                                   ThumbnailCompressionService compressor,
                                   CacheManager cacheManager) {
        this.videoRepository = videoRepository;
        this.compressor = compressor;
        this.cacheManager = cacheManager;
    }

    @Scheduled(cron = "0 50 13 * * *")
    public void run() {
        LocalDateTime cutoff = LocalDateTime.now().minus(1, ChronoUnit.DAYS);

        List<Video> videos = videoRepository.findThumbnailsToCompress(cutoff);

        for (Video v : videos) {
            try {
                compressOne(v);
                Cache cache = cacheManager.getCache("thumbnails");
                if (cache != null) {
                    cache.evict(v.getId());
                }
            } catch (Exception e) {
                org.slf4j.LoggerFactory.getLogger(ThumbnailCompressionJob.class)
                        .warn("Thumbnail compression failed for videoId={}", v.getId(), e);
            }
        }
    }

    private void compressOne(Video v) throws Exception {
        File input = new File(v.getThumbnailPath());
        if (!input.exists()) return;

        String outPath = buildCompressedPath(v.getThumbnailPath());
        File output = new File(outPath);

        if (!output.exists()) {
            compressor.compressToJpeg(input, output, 0.70);
        }

        v.setThumbnailCompressed(true);
        v.setThumbnailCompressedPath(outPath);
        videoRepository.save(v);
    }

    private String buildCompressedPath(String originalPath) {
        int dot = originalPath.lastIndexOf('.');
        String base = (dot > 0) ? originalPath.substring(0, dot) : originalPath;
        return base + "_compressed.jpg";
    }
}
