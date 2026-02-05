package com.example.jutjubic.controller;

import com.example.jutjubic.dto.*;
import com.example.jutjubic.mapper.DtoMapper;
import com.example.jutjubic.model.Video;
import com.example.jutjubic.service.CommentService;
import com.example.jutjubic.service.VideoLikeService;
import com.example.jutjubic.service.VideoService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;
    private final CommentService commentService;
    private final VideoLikeService videoLikeService;

    public VideoController(VideoService videoService, CommentService commentService, VideoLikeService videoLikeService) {
        this.videoService = videoService;
        this.commentService = commentService;
        this.videoLikeService = videoLikeService;
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Video> uploadVideo(
            @RequestPart("info") String infoJson,
            @RequestPart("thumbnail") MultipartFile thumbnail,
            @RequestPart("video") MultipartFile videoFile,
            HttpServletRequest request

    ) {


        Video video = videoService.uploadVideo(infoJson, thumbnail, videoFile, request);
        return ResponseEntity.ok(video);
    }




    @GetMapping
    public List<VideoPublicDto> getAll() {
        return videoService.findAllNewestFirst();
    }


    @GetMapping("/{id}")
    public ResponseEntity<VideoPublicDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.getDtoById(id));
    }



    @GetMapping(value = "/{id}/thumbnail")
    public ResponseEntity<byte[]> getThumbnail(@PathVariable Long id) {

        byte[] bytes = videoService.getThumbnailBytes(id);
        MediaType mediaType = videoService.getThumbnailMediaType(id);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.noCache())
                .body(bytes);
    }


    @GetMapping(value = "/{id}/stream", produces = "video/mp4")
    public ResponseEntity<ResourceRegion> streamVideo(
            @PathVariable Long id,
            @RequestHeader HttpHeaders headers
    ) throws Exception {

        Video v = videoService.getById(id);
        LocalDateTime now = LocalDateTime.now();
        if (v.isScheduled() && v.getScheduledAt() != null &&
                now.isBefore(v.getScheduledAt())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Path path = videoService.resolveStreamPath(v);
        if (path == null) {
            return ResponseEntity.notFound().build();
        }

        if (v.isScheduled() && v.getScheduledAt() != null) {
            Integer dur = v.getDurationSeconds();
            if (dur != null && dur > 0) {
                LocalDateTime end = v.getScheduledAt().plusSeconds(dur);
                if (!now.isBefore(end)) {

                    return ResponseEntity.status(HttpStatus.GONE)
                            .cacheControl(CacheControl.noStore())
                            .build();
                }
            }
        }

        Resource video = new FileSystemResource(path);

        long contentLength = video.contentLength();
        long chunkSize = 1_000_000; // 1MB (može 512KB-2MB)

        // Ako nema Range, pošalji prvi chunk (ili ceo fajl, ali chunk je bolje)
        if (headers.getRange() == null || headers.getRange().isEmpty()) {
            ResourceRegion region = new ResourceRegion(video, 0, Math.min(chunkSize, contentLength));
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .contentType(MediaType.valueOf("video/mp4"))
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .contentLength(region.getCount())
                    .body(region);
        }

        // Ako ima Range:
        HttpRange range = headers.getRange().get(0);
        long start = range.getRangeStart(contentLength);
        long end = range.getRangeEnd(contentLength);

        long rangeLength = Math.min(chunkSize, end - start + 1);

        ResourceRegion region = new ResourceRegion(video, start, rangeLength);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaType.valueOf("video/mp4"))
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .contentLength(region.getCount())
                .body(region);
    }


    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<CommentPublicDto>> getComments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        if (size < 1) size = 1;
        if (size > 100) size = 100;

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CommentPublicDto> result = commentService.getForVideoPaged(id, pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentPublicDto> addComment(
            @PathVariable Long id,
            @RequestBody CommentCreateRequest req
    ) {
        CommentPublicDto created = commentService.addComment(id, req.text());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/like")
    public long like(@PathVariable Long id, Authentication auth) {
        if (auth == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Morate biti ulogovani");
        }
        return videoLikeService.like(id, auth.getName());
    }

    @DeleteMapping("/{id}/like")
    public long unlike(@PathVariable Long id, Authentication auth) {
        if (auth == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Morate biti ulogovani");
        }
        return videoLikeService.unlike(id, auth.getName());
    }

    @GetMapping("/{id}/like")
    public boolean isLiked(@PathVariable Long id, Authentication auth) {
        if (auth == null) return false;
        return videoLikeService.isLiked(id, auth.getName());
    }

    @GetMapping("/{id}/likes/count")
    public long likeCount(@PathVariable Long id) {
        return videoLikeService.countForVideo(id);
    }

    @GetMapping("/{id}/watch-info")
    public ResponseEntity<WatchInfoDto> watchInfo(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.watchInfo(id));
    }

    @PostMapping("/{id}/premiere-ended")
    public ResponseEntity<Void> markPremiereEnded(@PathVariable Long id) {
        videoService.markPremiereEnded(id);
        return ResponseEntity.noContent().build();
    }




}
