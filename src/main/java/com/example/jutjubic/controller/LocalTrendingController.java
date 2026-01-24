package com.example.jutjubic.controller;

import com.example.jutjubic.dto.VideoPublicDto;
import com.example.jutjubic.service.LocalTrendingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trending")
public class LocalTrendingController {

    private final LocalTrendingService localTrendingService;

    public LocalTrendingController(LocalTrendingService localTrendingService) {
        this.localTrendingService = localTrendingService;
    }

    @GetMapping
    public List<VideoPublicDto> local(
            @RequestParam double radiusKm,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            HttpServletRequest request
    ) {
        try {
            return localTrendingService.getLocalTrending(radiusKm, lat, lon, request);
        } catch (Exception e) {
            e.printStackTrace(); // privremeno
            throw e;
        }
    }
}
