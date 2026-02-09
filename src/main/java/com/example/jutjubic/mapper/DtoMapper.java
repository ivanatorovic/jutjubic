package com.example.jutjubic.mapper;

import com.example.jutjubic.dto.CommentPublicDto;
import com.example.jutjubic.dto.PremiereStatus;
import com.example.jutjubic.dto.UserPublicDto;
import com.example.jutjubic.dto.VideoPublicDto;
import com.example.jutjubic.model.Comment;
import com.example.jutjubic.model.User;
import com.example.jutjubic.model.Video;

import java.time.LocalDateTime;
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
    private static PremiereStatus computePremiereStatus(Video v, LocalDateTime now) {
        if (!v.isScheduled() || v.getScheduledAt() == null) return null;

        // ako je ručno markirano da je završena
        if (v.isPremiereEnded()) return PremiereStatus.ENDED;

        LocalDateTime start = v.getScheduledAt();

        if (now.isBefore(start)) return PremiereStatus.SCHEDULED;

        Integer dur = v.getDurationSeconds();
        if (dur != null && dur > 0) {
            LocalDateTime end = start.plusSeconds(dur);
            if (!now.isBefore(end)) return PremiereStatus.ENDED;
        }

        return PremiereStatus.LIVE;
    }

    public static VideoPublicDto toVideoPublicDto(Video v, long likeCount, long commentCount , LocalDateTime now) {
        Long userId = null;
        String username = null;

        if (v.getUser() != null) {
            userId = v.getUser().getId();
            username = v.getUser().getUsername();
        }

        PremiereStatus st = computePremiereStatus(v, now);
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
                v.getViewCount(),

                v.getScheduledAt(),
                v.getDurationSeconds(),
                st
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

    public static VideoPublicDto toVideoPublicDto(Video v, long likeCount, long commentCount) {
        return toVideoPublicDto(v, likeCount, commentCount, LocalDateTime.now());
    }

}
