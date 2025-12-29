package com.example.jutjubic.controller;

import com.example.jutjubic.model.Video;
import com.example.jutjubic.repository.VideoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private final VideoRepository videoRepository;

    public VideoController(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    // GET /videos  -> svi videi
    @GetMapping
    public List<Video> getAll() {
        return videoRepository.findAll();
    }

    // POST /videos  -> kreira novi video
    @PostMapping
    public Video create(@RequestBody Video video) {
        return videoRepository.save(video);
    }
}
