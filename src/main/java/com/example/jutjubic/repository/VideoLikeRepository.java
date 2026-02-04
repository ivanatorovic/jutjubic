package com.example.jutjubic.repository;

import com.example.jutjubic.model.VideoLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VideoLikeRepository extends JpaRepository<VideoLike, Long> {

    boolean existsByVideo_IdAndUser_Id(Long videoId, Long userId);

    Optional<VideoLike> findByVideo_IdAndUser_Id(Long videoId, Long userId);


    long countByVideoId(Long videoId);


    @Query("""
        select vl.video.id as videoId, count(vl.id) as cnt
        from VideoLike vl
        where vl.video.id in :videoIds
        group by vl.video.id
    """)
    List<IdCount> countLikesByVideoIds(@Param("videoIds") List<Long> videoIds);

    interface IdCount {
        Long getVideoId();
        Long getCnt();
    }
}
