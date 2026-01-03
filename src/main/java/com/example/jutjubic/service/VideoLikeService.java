package com.example.jutjubic.service;

import com.example.jutjubic.repository.VideoLikeRepository;
import org.springframework.stereotype.Service;

@Service
public class VideoLikeService {

    private final VideoLikeRepository videoLikeRepository;

    public VideoLikeService(VideoLikeRepository videoLikeRepository) {
        this.videoLikeRepository = videoLikeRepository;
    }

    /**
     * Samo broj lajkova za video
     */
    public long countForVideo(Long videoId) {
        return videoLikeRepository.countByVideoId(videoId);
    }
}
