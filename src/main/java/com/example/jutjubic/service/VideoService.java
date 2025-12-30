package com.example.jutjubic.service;

import com.example.jutjubic.model.Video;
import com.example.jutjubic.repository.VideoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService {

    private final VideoRepository videoRepository;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public List<Video> findAllNewestFirst() {
        // Ako imaš createdAt:
        // return videoRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        // Ako nemaš createdAt (fallback):
        return videoRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

}
