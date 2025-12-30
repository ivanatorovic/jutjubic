package com.example.jutjubic.controller;

import com.example.jutjubic.model.Video;
import com.example.jutjubic.repository.VideoRepository;
import com.example.jutjubic.service.VideoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public List<Video> getAll() {
        return videoService.findAllNewestFirst();
    }
}

