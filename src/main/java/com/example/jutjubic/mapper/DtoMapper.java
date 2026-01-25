package com.example.jutjubic.mapper;

import com.example.jutjubic.dto.CommentPublicDto;
import com.example.jutjubic.dto.UserPublicDto;
import com.example.jutjubic.dto.VideoPublicDto;
import com.example.jutjubic.model.Comment;
import com.example.jutjubic.model.User;
import com.example.jutjubic.model.Video;

import java.util.List;

public class DtoMapper {

    private DtoMapper() {}

    public static UserPublicDto toUserPublicDto(User u) {
        return new UserPublicDto(
                u.getId(),
                u.getUsername(),
                u.getFirstName(),
                u.getLastName(),
                u.getAddress(),
                u.getCreatedAt()
        );
    }

    public static VideoPublicDto toVideoPublicDto(Video v, long likeCount, long commentCount) {
        Long userId = null;
        String username = null;

        if (v.getUser() != null) {
            userId = v.getUser().getId();
            username = v.getUser().getUsername();
        }

        // ✅ KLJUČNA IZMENA: napravi "plain" listu (ne Hibernate lazy kolekciju/proxy)
        List<String> safeTags = (v.getTags() == null) ? List.of() : List.copyOf(v.getTags());

        return new VideoPublicDto(
                v.getId(),
                v.getTitle(),
                v.getDescription(),
                safeTags,
                v.getSizeMB(),
                v.getCreatedAt(),
                v.getLocation(),
                userId,
                username,
                likeCount,
                commentCount,
                v.getViewCount()
        );
    }

    public static CommentPublicDto toCommentPublicDto(Comment c) {
        Long userId = null;
        String username = null;

        if (c.getUser() != null) {
            userId = c.getUser().getId();
            username = c.getUser().getUsername();
        }

        return new CommentPublicDto(
                c.getId(),
                c.getText(),
                userId,
                username,
                c.getCreatedAt()
        );
    }
}
