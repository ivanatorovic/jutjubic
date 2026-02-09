package com.example.jutjubic.service;
import com.example.jutjubic.config.RabbitConfig;
import com.example.jutjubic.messaging.TranscodeRequestMessage;
import com.example.jutjubic.dto.VideoPublicDto;
import com.example.jutjubic.dto.VideoUploadRequest;
import com.example.jutjubic.mapper.DtoMapper;
import com.example.jutjubic.exception.BadRequestException;
import com.example.jutjubic.exception.InternalException;
import com.example.jutjubic.exception.NotFoundException;
import com.example.jutjubic.model.TranscodeJob;
import com.example.jutjubic.model.Video;
import com.example.jutjubic.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
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
import jakarta.servlet.http.HttpServletRequest;
import com.example.jutjubic.util.GeoHash;



import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.example.jutjubic.dto.WatchInfoDto;
import com.example.jutjubic.dto.PremiereStatus;

@Service
public class VideoService {
    private static final Logger LOG =
            org.slf4j.LoggerFactory.getLogger(VideoService.class);

    private final UserRepository userRepository;
    private final VideoRepository videoRepository;
    private final ObjectMapper objectMapper;
    private final VideoLikeService videoLikeService;
    private final CommentService commentService;
    private final IpGeoService ipGeoService;
    private final TranscodeJobRepository transcodeJobRepository;
    private final org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate;
    private final VideoDailyViewsRepository dailyViewsRepo;


    private static final long MAX_VIDEO_SIZE_BYTES = 200L * 1024 * 1024;

    private static final String VIDEO_DIR = "storage/videos";
    private static final String THUMB_DIR = "storage/thumbnails";

    public VideoService(VideoRepository videoRepository,
                        ObjectMapper objectMapper,
                        VideoLikeService videoLikeService,
                        CommentService commentService,
                        UserRepository userRepository,
                        IpGeoService ipGeoService, TranscodeJobRepository transcodeJobRepository, org.springframework.amqp.rabbit.core.RabbitTemplate rabbitTemplate, VideoDailyViewsRepository dailyViewsRepo) {
        this.videoRepository = videoRepository;
        this.objectMapper = objectMapper;
        this.videoLikeService = videoLikeService;
        this.commentService = commentService;
        this.userRepository = userRepository;
        this.ipGeoService = ipGeoService;
        this.transcodeJobRepository = transcodeJobRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.dailyViewsRepo = dailyViewsRepo;
    }


    @Transactional
    public Video uploadVideo(String infoJson,
                             MultipartFile thumbnailFile,
                             MultipartFile videoFile,
                             HttpServletRequest request) {

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
            if (info.isScheduled()) {
                if (info.getScheduledAt() == null) {
                    throw new BadRequestException("Morate uneti datum i vreme za zakazani video.");
                }
                if (info.getScheduledAt().isBefore(LocalDateTime.now())) {
                    throw new BadRequestException("Zakazano vreme mora biti u budućnosti.");
                }
                video.setScheduled(true);
                video.setScheduledAt(info.getScheduledAt());
            } else {
                video.setScheduled(false);
                video.setScheduledAt(null);
            }


            if (info.getLatitude() != null && info.getLongitude() != null) {
                video.setLatitude(info.getLatitude());
                video.setLongitude(info.getLongitude());
            } else {
                String ip = extractClientIp(request);
                IpGeoService.GeoPoint p = ipGeoService.locate(ip);
                video.setLatitude(p.lat());
                video.setLongitude(p.lon());
            }

            int geoPrecision = 8;
            video.setGeohash(
                    GeoHash.encode(video.getLatitude(), video.getLongitude(), geoPrecision)
            );


            int dur = com.example.jutjubic.util.FfprobeUtil.probeDurationSeconds(Paths.get(savedVideoPath));
            video.setDurationSeconds(dur > 0 ? dur : null);
            video.setDurationSeconds(dur);
            Video saved = videoRepository.save(video);

            TranscodeJob job = new TranscodeJob(saved, saved.getVideoPath());
            TranscodeJob savedJob = transcodeJobRepository.save(job);

            saved.setTranscodeStatus(Video.TranscodeStatus.TRANSCODING);
            videoRepository.save(saved);

            TranscodeRequestMessage msg = new TranscodeRequestMessage(
                    savedJob.getJobId(),
                    saved.getId(),
                    saved.getVideoPath()
            );

            rabbitTemplate.convertAndSend(
                    RabbitConfig.TRANSCODE_EXCHANGE,
                    RabbitConfig.TRANSCODE_ROUTING_KEY,
                    msg
            );


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

        String path = null;

        if (v.isThumbnailCompressed() && v.getThumbnailCompressedPath() != null
                && !v.getThumbnailCompressedPath().isBlank()) {
            path = v.getThumbnailCompressedPath();
        } else if (v.getThumbnailPath() != null && !v.getThumbnailPath().isBlank()) {
            path = v.getThumbnailPath();
        }

        if (path == null) {
            throw new RuntimeException("Video nema thumbnail.");
        }

        try {
            org.slf4j.LoggerFactory.getLogger(VideoService.class)
                    .info("Reading thumbnail from disk for videoId={}, path={}", id, path);

            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            throw new RuntimeException("Ne mogu da pročitam thumbnail sa diska.", e);
        }
    }

    public MediaType getThumbnailMediaType(Long id) {
        Video v = getById(id);
        String path = resolveThumbnailPath(v);

        String p = path.toLowerCase();
        if (p.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (p.endsWith(".jpg") || p.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;

        return MediaType.IMAGE_JPEG;
    }

    private String resolveThumbnailPath(Video v) {
        if (v.isThumbnailCompressed()
                && v.getThumbnailCompressedPath() != null
                && !v.getThumbnailCompressedPath().isBlank()) {
            return v.getThumbnailCompressedPath();
        }
        return v.getThumbnailPath();
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
        LocalDateTime now = LocalDateTime.now();

        List<Video> videos = videoRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        return videos.stream()
                .map(v -> {
                    long likeCount = videoLikeService.countForVideo(v.getId());
                    long commentCount = commentService.countForVideo(v.getId());
                    return DtoMapper.toVideoPublicDto(v, likeCount, commentCount, now);
                })
                .toList();
    }


    public void registerView(Long videoId) {
        int updated = videoRepository.incrementViewCount(videoId);
        if (updated == 0) {
            throw new NotFoundException("Video sa id=" + videoId + " ne postoji.");

        }
        dailyViewsRepo.incrementToday(videoId);
    }
    public VideoPublicDto getDtoById(Long id) {
        Video v = getById(id);

        // scheduled gate:
        if (v.isScheduled() && v.getScheduledAt() != null && LocalDateTime.now().isBefore(v.getScheduledAt())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Video još nije dostupan.");
        }

        registerView(id);

        return DtoMapper.toVideoPublicDto(
                v,
                videoLikeService.countForVideo(v.getId()),
                commentService.countForVideo(v.getId()),
                LocalDateTime.now()
        );
    }



    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    public Path resolveStreamPath(Video v) {
        if (v.getTranscodeStatus() == Video.TranscodeStatus.READY
                && v.getTranscodedPath() != null
                && !v.getTranscodedPath().isBlank()) {
            return Paths.get(v.getTranscodedPath());
        }

        if (v.getVideoPath() != null && !v.getVideoPath().isBlank()) {
            return Paths.get(v.getVideoPath());
        }

        return null;
    }
    public void markPremiereEnded(Long id) {
        Video v = videoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!v.isScheduled()) {
            return;
        }
        v.setPremiereEnded(true);
        videoRepository.save(v);
    }


    public WatchInfoDto watchInfo(Long id) {
        Video v = getById(id);

        LocalDateTime now = LocalDateTime.now();


        if (!v.isScheduled() || v.getScheduledAt() == null) {
            return new WatchInfoDto(
                    now,
                    null,
                    v.getDurationSeconds(),
                    PremiereStatus.LIVE
            );
        }


        LocalDateTime start = v.getScheduledAt();
        Integer dur = v.getDurationSeconds();

        PremiereStatus status;
        if (now.isBefore(start)) {
            status = PremiereStatus.SCHEDULED;
        } else if (dur != null && dur > 0 && !now.isBefore(start.plusSeconds(dur))) {
            status = PremiereStatus.ENDED;
            if (!v.isPremiereEnded()) {
                v.setPremiereEnded(true);
                videoRepository.save(v);
            }
        } else {
            status = PremiereStatus.LIVE;
        }

        return new WatchInfoDto(now, start, dur, status);
    }




}
