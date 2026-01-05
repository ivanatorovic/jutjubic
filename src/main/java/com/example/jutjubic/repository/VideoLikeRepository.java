package com.example.jutjubic.repository;

import com.example.jutjubic.model.VideoLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoLikeRepository extends JpaRepository<VideoLike, Long> {

    boolean existsByVideo_IdAndUser_Id(Long videoId, Long userId);

    Optional<VideoLike> findByVideo_IdAndUser_Id(Long videoId, Long userId);

    long countByVideoId(Long videoId); // TI VEĆ OVO KORISTIŠ U UserService
}
