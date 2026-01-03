package com.example.jutjubic.service;

import com.example.jutjubic.dto.CommentPublicDto;
import com.example.jutjubic.mapper.DtoMapper;
import com.example.jutjubic.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<CommentPublicDto> getForVideo(Long videoId) {
        return commentRepository.findByVideoIdOrderByCreatedAtDesc(videoId).stream()
                .map(DtoMapper::toCommentPublicDto)
                .toList();
    }

    public long countForVideo(Long videoId) {
        return commentRepository.countByVideoId(videoId);
    }
}
