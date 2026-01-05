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

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          VideoRepository videoRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
    }

    /**
     * ✅ Keš za paginirane komentare:
     * key uključuje videoId + page + size + sort
     */
    @Cacheable(
            cacheNames = "videoComments",
            key = "#videoId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort.toString()"
    )
    public Page<CommentPublicDto> getForVideoPaged(Long videoId, Pageable pageable) {
        return commentRepository.findByVideoIdOrderByCreatedAtDesc(videoId, pageable)
                .map(DtoMapper::toCommentPublicDto);
    }

    @Cacheable(cacheNames = "videoCommentCount", key = "#videoId")
    public long countForVideo(Long videoId) {
        return commentRepository.countByVideoId(videoId);
    }

    /**
     * ✅ Kad dodaš komentar:
     * - count za taj videoId obriši ciljano (key = videoId)
     * - paginirani keš obriši ceo (allEntries=true), jer se pomeraju strane
     */
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

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new NotFoundException("Video sa id=" + videoId + " ne postoji."));

        Comment comment = new Comment(text.trim(), video, user);
        Comment saved = commentRepository.save(comment);

        return DtoMapper.toCommentPublicDto(saved);
    }
}
