package com.example.jutjubic.repository;

import com.example.jutjubic.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    long countByVideoId(Long videoId);
    Page<Comment> findByVideoIdOrderByCreatedAtDesc(Long videoId, Pageable pageable);

}
