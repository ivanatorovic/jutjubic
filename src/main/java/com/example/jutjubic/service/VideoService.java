package com.example.jutjubic.service;

import com.example.jutjubic.dto.VideoPublicDto;
import com.example.jutjubic.dto.VideoUploadRequest;
import com.example.jutjubic.mapper.DtoMapper;
import com.example.jutjubic.model.Video;
import com.example.jutjubic.repository.CommentRepository;
import com.example.jutjubic.repository.UserRepository;
import com.example.jutjubic.repository.VideoLikeRepository;
import com.example.jutjubic.repository.VideoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final ObjectMapper objectMapper;
    private final VideoLikeService videoLikeService;
    private final CommentService commentService;

    private static final long MAX_VIDEO_SIZE_BYTES = 200L * 1024 * 1024;

    private static final String VIDEO_DIR = "storage/videos";
    private static final String THUMB_DIR = "storage/thumbnails";

    public VideoService(VideoRepository videoRepository, ObjectMapper objectMapper, VideoLikeService videoLikeService, CommentService commentService) {
        this.videoRepository = videoRepository;
        this.objectMapper = objectMapper;
        this.videoLikeService = videoLikeService;
        this.commentService = commentService;
    }

    /**
     * Transakciono kreiranje video objave.
     * - DB operacije su u transakciji (rollback na RuntimeException)
     * - Fajlovi na disku NISU deo transakcije, pa ih ručno brišemo u catch-u.
     */
    @Transactional
    public Video uploadVideo(String infoJson,
                             MultipartFile thumbnailFile,
                             MultipartFile videoFile) {

        long start = System.currentTimeMillis();

        // 1) Parsiranje JSON-a
        final VideoUploadRequest info;
        try {
            info = objectMapper.readValue(infoJson, VideoUploadRequest.class);
        } catch (IOException e) {
            throw new RuntimeException("Nevalidan JSON u polju 'info'.", e);
        }

        // 2) Validacije
        if (videoFile == null || videoFile.isEmpty()) {
            throw new RuntimeException("Video fajl je obavezan.");
        }

        String originalVideoName = videoFile.getOriginalFilename();
        if (originalVideoName == null || !originalVideoName.toLowerCase().endsWith(".mp4")) {
            throw new RuntimeException("Video mora biti u .mp4 formatu.");
        }

        if (videoFile.getSize() > MAX_VIDEO_SIZE_BYTES) {
            throw new RuntimeException("Video fajl je veći od 200MB.");
        }

        // Ako ti specifikacija traži thumbnail kao obavezan:
        // (ako želiš da bude strict po zahtevu 3.3)
        if (thumbnailFile == null || thumbnailFile.isEmpty()) {
            throw new RuntimeException("Thumbnail slika je obavezna.");
        }

        // Ove promenljive čuvamo da bismo mogli da obrišemo fajlove ako pukne posle snimanja
        String savedVideoPath = null;
        String savedThumbPath = null;

        try {
            // 3) Snimi fajlove na disk
            savedVideoPath = saveFile(videoFile, VIDEO_DIR);
            savedThumbPath = saveFile(thumbnailFile, THUMB_DIR);

            // 4) Kreiraj entitet
            Video video = new Video();
            video.setTitle(info.getTitle());
            video.setDescription(info.getDescription());
            video.setTags(info.getTags());
            video.setLocation(info.getLocation());
            video.setVideoPath(savedVideoPath);
            video.setThumbnailPath(savedThumbPath);
            video.setSizeMB(videoFile.getSize() / 1024 / 1024);
            video.setCreatedAt(LocalDateTime.now());

            // 5) Upis u bazu (u transakciji)
            Video saved = videoRepository.save(video);

            // 6) Test "upload traje predugo" -> izazovi rollback
            long duration = System.currentTimeMillis() - start;
            if (duration > 10_000) {
                throw new RuntimeException("Upload je trajao predugo -> rollback.");
            }

            return saved;

        } catch (Exception e) {
            // 7) RUČNO brisanje fajlova, da rollback bude "kompletan"
            safeDelete(savedVideoPath);
            safeDelete(savedThumbPath);

            // DB rollback će se desiti jer bacamo RuntimeException
            throw new RuntimeException("Greška tokom upload-a -> rollback aktiviran!", e);
        }
    }

    public List<Video> getAll() {
        return videoRepository.findAll();
    }

    public Video getById(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video sa id=" + id + " ne postoji."));
    }

    /**
     * Thumbnail bytes se keširaju u memoriji.
     * Prvi put čita sa diska -> posle vraća iz cache-a (bez diska).
     */
    @Cacheable(cacheNames = "thumbnails", key = "#id")
    public byte[] getThumbnailBytes(Long id) {
        Video v = getById(id);

        if (v.getThumbnailPath() == null || v.getThumbnailPath().isBlank()) {
            throw new RuntimeException("Video nema thumbnail.");
        }

        try {
            return Files.readAllBytes(Paths.get(v.getThumbnailPath()));
        } catch (IOException e) {
            throw new RuntimeException("Ne mogu da pročitam thumbnail sa diska.", e);
        }
    }

    /**
     * Ako ikad budeš menjala thumbnail nekog videa, pozovi ovu metodu (ili napravi endpoint)
     * da očisti keš.
     */
    @CacheEvict(cacheNames = "thumbnails", key = "#id")
    public void evictThumbnailCache(Long id) {
        // namerno prazno - anotacija radi posao
    }

    // ----------------- helpers -----------------

    private String saveFile(MultipartFile file, String directory) throws IOException {
        Path dirPath = Paths.get(directory);
        Files.createDirectories(dirPath);

        String extension = "";
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = UUID.randomUUID() + extension;
        Path fullPath = dirPath.resolve(filename);

        // REPLACE_EXISTING da ti ne pukne ako ikad (teoretski) dođe do istog imena
        Files.copy(file.getInputStream(), fullPath, StandardCopyOption.REPLACE_EXISTING);

        return fullPath.toString();
    }

    private void safeDelete(String pathStr) {
        if (pathStr == null) return;
        try {
            Files.deleteIfExists(Paths.get(pathStr));
        } catch (IOException ignored) {
            // namerno ignorišemo da ne prekrije originalnu grešku
        }
    }

    public List<VideoPublicDto> findAllNewestFirst() {
        List<Video> videos = videoRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return videos.stream()
                .map(v -> DtoMapper.toVideoPublicDto(
                        v,
                        videoLikeService.countForVideo(v.getId()),
                        commentService.countForVideo(v.getId())
                ))
                .toList();
    }


    public VideoPublicDto getDtoById(Long id) {
        Video v = getById(id);
        return DtoMapper.toVideoPublicDto(
                v,
                videoLikeService.countForVideo(v.getId()),
                commentService.countForVideo(v.getId())
        );
    }



}
