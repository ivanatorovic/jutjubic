package com.example.jutjubic.service;
import com.example.jutjubic.dto.VideoPublicDto;
import com.example.jutjubic.dto.VideoUploadRequest;
import com.example.jutjubic.mapper.DtoMapper;
import com.example.jutjubic.exception.BadRequestException;
import com.example.jutjubic.exception.InternalException;
import com.example.jutjubic.exception.NotFoundException;
import com.example.jutjubic.model.Video;
import com.example.jutjubic.repository.CommentRepository;
import com.example.jutjubic.repository.UserRepository;
import com.example.jutjubic.repository.VideoLikeRepository;
import com.example.jutjubic.repository.VideoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VideoService {
    private static final Logger LOG =
            org.slf4j.LoggerFactory.getLogger(VideoService.class);

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final ObjectMapper objectMapper;
    private final VideoLikeService videoLikeService;
    private final CommentService commentService;

    private static final long MAX_VIDEO_SIZE_BYTES = 200L * 1024 * 1024;

    private static final String VIDEO_DIR = "storage/videos";
    private static final String THUMB_DIR = "storage/thumbnails";

    public VideoService(VideoRepository videoRepository,
                        ObjectMapper objectMapper,
                        VideoLikeService videoLikeService,
                        CommentService commentService,
                        UserRepository userRepository) {
        this.videoRepository = videoRepository;
        this.objectMapper = objectMapper;
        this.videoLikeService = videoLikeService;
        this.commentService = commentService;
        this.userRepository = userRepository;
    }


    @Transactional
    public Video uploadVideo(String infoJson,
                             MultipartFile thumbnailFile,
                             MultipartFile videoFile) {

        long start = System.currentTimeMillis();

        final VideoUploadRequest info;
        try {
            info = objectMapper.readValue(infoJson, VideoUploadRequest.class);
        } catch (IOException e) {
            throw new BadRequestException("Nevalidan JSON u polju 'info'.", e);
        }


        if (videoFile == null || videoFile.isEmpty()) {
            throw new BadRequestException("Video fajl je obavezan.");
        }

        String originalVideoName = videoFile.getOriginalFilename();
        if (originalVideoName == null || !originalVideoName.toLowerCase().endsWith(".mp4")) {
            throw new BadRequestException("Video mora biti u .mp4 formatu.");
        }

        if (videoFile.getSize() > MAX_VIDEO_SIZE_BYTES) {
            throw new BadRequestException("Video fajl je veći od 200MB.");
        }


        if (thumbnailFile == null || thumbnailFile.isEmpty()) {
            throw new BadRequestException("Thumbnail slika je obavezna.");
        }


        if (info.getTitle() == null || info.getTitle().trim().isEmpty()
                || info.getDescription() == null || info.getDescription().trim().isEmpty()
                || info.getTags() == null || info.getTags().isEmpty()
                || info.getTags().stream().anyMatch(t -> t == null || t.trim().isEmpty())) {
            throw new BadRequestException("Title, opis i tagovi moraju biti popunjeni.");
        }

        String savedVideoPath = null;
        String savedThumbPath = null;

        try {

            savedVideoPath = saveFile(videoFile, VIDEO_DIR);
            savedThumbPath = saveFile(thumbnailFile, THUMB_DIR);
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String principalName = auth.getName();
            var uploader = userRepository.findByEmail(principalName)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Korisnik ne postoji."));

            Video video = new Video();
            video.setTitle(info.getTitle());
            video.setDescription(info.getDescription());
            video.setTags(info.getTags());
            video.setLocation(info.getLocation());
            video.setVideoPath(savedVideoPath);
            video.setThumbnailPath(savedThumbPath);
            video.setSizeMB(videoFile.getSize() / 1024 / 1024);
            video.setCreatedAt(LocalDateTime.now());
            video.setUser(uploader);


            Video saved = videoRepository.save(video);
            boolean testRollback = false;
            if (testRollback) {
                throw new RuntimeException("Test rollback");
            }
            //Thread.sleep(11_000);

            long duration = System.currentTimeMillis() - start;
            if (duration > 10_000) {
                LOG.error("Upload je trajao predugo ({} ms) → rollback", duration);
                throw new RuntimeException("Upload je trajao predugo -> rollback.");
            }

            return saved;

        } catch (Exception e) {
            safeDelete(savedVideoPath);
            safeDelete(savedThumbPath);


            throw (e instanceof RuntimeException re) ? re
                    : new InternalException("Greška tokom upload-a -> rollback aktiviran!", e);
        }
    }

    public List<Video> getAll() {
        return videoRepository.findAll();
    }

    public Video getById(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Video sa id=" + id + " ne postoji."));
    }


    @Cacheable(cacheNames = "thumbnails", key = "#id")
    public byte[] getThumbnailBytes(Long id) {
        Video v = getById(id);

        if (v.getThumbnailPath() == null || v.getThumbnailPath().isBlank()) {
            throw new RuntimeException("Video nema thumbnail.");
        }

        try {
            org.slf4j.LoggerFactory.getLogger(VideoService.class)
                    .info("Reading thumbnail from disk for videoId={}", id);

            return Files.readAllBytes(Paths.get(v.getThumbnailPath()));
        } catch (IOException e) {
            throw new RuntimeException("Ne mogu da pročitam thumbnail sa diska.", e);
        }
    }



    @CacheEvict(cacheNames = "thumbnails", key = "#id")
    public void evictThumbnailCache(Long id) {

    }



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


        Files.copy(file.getInputStream(), fullPath, StandardCopyOption.REPLACE_EXISTING);

        return fullPath.toString();
    }

    private void safeDelete(String pathStr) {
        if (pathStr == null) return;
        try {
            Files.deleteIfExists(Paths.get(pathStr));
        } catch (IOException ignored) {

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

    public void registerView(Long videoId) {
        int updated = videoRepository.incrementViewCount(videoId);
        if (updated == 0) {
            throw new NotFoundException("Video sa id=" + videoId + " ne postoji.");
        }
    }
    public VideoPublicDto getDtoById(Long id) {

        registerView(id);

        Video v = getById(id);
        return DtoMapper.toVideoPublicDto(
                v,
                videoLikeService.countForVideo(v.getId()),
                commentService.countForVideo(v.getId())
        );
    }





}
