package com.example.jutjubic.service;

import com.example.jutjubic.dto.CommentPublicDto;
import com.example.jutjubic.exception.BadRequestException;
import com.example.jutjubic.exception.NotFoundException;
import com.example.jutjubic.mapper.DtoMapper;
import com.example.jutjubic.model.Comment;
import com.example.jutjubic.model.Video;
import com.example.jutjubic.repository.CommentRepository;
import com.example.jutjubic.repository.UserRepository;
import com.example.jutjubic.repository.VideoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public CommentService(CommentRepository commentRepository,
                          VideoRepository videoRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
    }

    private Bucket resolveBucket(String userKey) {
        return buckets.computeIfAbsent(userKey, k -> {
            Bandwidth limit = Bandwidth.classic(60, Refill.intervally(60, Duration.ofHours(1)));
            return Bucket.builder().addLimit(limit).build();
        });
    }

    @Cacheable(
            cacheNames = "videoComments",
            key = "#videoId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort.toString()"
    )
    public Page<CommentPublicDto> getForVideoPaged(Long videoId, Pageable pageable) {
        return commentRepository.findByVideoIdOrderByCreatedAtDesc(videoId, pageable)
                .map(DtoMapper::toCommentPublicDto);
    }

    public long countForVideo(Long videoId) {
        return commentRepository.countByVideoId(videoId);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = "videoCommentCount", key = "#videoId"),
            @CacheEvict(cacheNames = "videoComments", allEntries = true)
    })
    public CommentPublicDto addComment(Long videoId, String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new BadRequestException("Komentar ne može biti prazan.");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth instanceof AnonymousAuthenticationToken || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Moraš biti ulogovan da bi komentarisao.");
        }

        var user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Korisnik ne postoji."));

        String key = user.getId().toString();
        Bucket bucket = resolveBucket(key);

        if (!bucket.tryConsume(1)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Previše komentara. Dozvoljeno je 60 komentara na sat po nalogu.");
        }

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new NotFoundException("Video sa id=" + videoId + " ne postoji."));

        Comment comment = new Comment(text.trim(), video, user);
        Comment saved = commentRepository.save(comment);

        return DtoMapper.toCommentPublicDto(saved);
    }
}
