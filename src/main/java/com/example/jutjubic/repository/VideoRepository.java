package com.example.jutjubic.repository;

import com.example.jutjubic.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Long>, VideoRepositoryCustom {
    List<Video> findByUserIdOrderByCreatedAtDesc(Long userId);


}
