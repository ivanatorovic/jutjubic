package com.example.jutjubic.controller;

import com.example.jutjubic.dto.CommentPublicDto;
import com.example.jutjubic.dto.VideoPublicDto;
import com.example.jutjubic.mapper.DtoMapper;
import com.example.jutjubic.model.Video;
import com.example.jutjubic.service.CommentService;
import com.example.jutjubic.service.VideoService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;
    private final CommentService commentService;

    public VideoController(VideoService videoService, CommentService commentService) {
        this.videoService = videoService;
        this.commentService = commentService;
    }

    // 1) Upload (multipart/form-data)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Video> uploadVideo(
            @RequestPart("info") String infoJson,
            @RequestPart("thumbnail") MultipartFile thumbnail,     // sad je obavezno
            @RequestPart("video") MultipartFile videoFile
    ) {
        Video video = videoService.uploadVideo(infoJson, thumbnail, videoFile);
        return ResponseEntity.ok(video);
    }

    // 2) Lista svih video objava (da drugi korisnici vide novu objavu)

    @GetMapping
    public List<VideoPublicDto> getAll() {
        return videoService.findAllNewestFirst();
    }

    // 3) Jedan video po id (metadata)
    @GetMapping("/{id}")
    public ResponseEntity<VideoPublicDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(videoService.getDtoById(id));
    }


    // 4) Vrati thumbnail bytes (ovo koristi cache u VideoService)
    @GetMapping(value = "/{id}/thumbnail")
    public ResponseEntity<byte[]> getThumbnail(@PathVariable Long id) {
        byte[] bytes = videoService.getThumbnailBytes(id);

        // probaj da odrediš content-type po ekstenziji, ako može
        Video v = videoService.getById(id);
        MediaType mediaType = MediaType.IMAGE_JPEG;
        if (v.getThumbnailPath() != null) {
            String p = v.getThumbnailPath().toLowerCase();
            if (p.endsWith(".png")) mediaType = MediaType.IMAGE_PNG;
            else if (p.endsWith(".jpg") || p.endsWith(".jpeg")) mediaType = MediaType.IMAGE_JPEG;
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .cacheControl(CacheControl.noCache()) // može i maxAge ako želiš
                .body(bytes);
    }

    // 5) Najjednostavniji endpoint za puštanje mp4 (bez range/seek podrške)
    @GetMapping(value = "/{id}/stream", produces = "video/mp4")
    public ResponseEntity<Resource> streamVideo(@PathVariable Long id) {
        Video v = videoService.getById(id);

        if (v.getVideoPath() == null || v.getVideoPath().isBlank()) {
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get(v.getVideoPath());
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(path);

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("video/mp4"))
                .body(resource);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentPublicDto>> getComments(@PathVariable Long id) {
        List<CommentPublicDto> comments = commentService.getForVideo(id);
        return ResponseEntity.ok(comments);
    }

}
