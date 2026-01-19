package com.example.jutjubic.repository;

import com.example.jutjubic.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    long countByVideoId(Long videoId);

    Page<Comment> findByVideoIdOrderByCreatedAtDesc(Long videoId, Pageable pageable);

    // ✅ batch counts za trending (bez N+1)
    @Query("""
        select c.video.id as videoId, count(c.id) as cnt
        from Comment c
        where c.video.id in :videoIds
        group by c.video.id
    """)
    List<IdCount> countCommentsByVideoIds(@Param("videoIds") List<Long> videoIds);

    interface IdCount {
        Long getVideoId();
        Long getCnt();
    }
}
