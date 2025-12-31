package com.example.jutjubic.repository;

import com.example.jutjubic.model.VideoLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoLikeRepository extends JpaRepository<VideoLike, Long> {
    long countByVideoId(Long videoId);
}
