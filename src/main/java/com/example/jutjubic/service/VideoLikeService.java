package com.example.jutjubic.service;

import com.example.jutjubic.model.User;
import com.example.jutjubic.model.Video;
import com.example.jutjubic.model.VideoLike;
import com.example.jutjubic.repository.VideoLikeRepository;
import com.example.jutjubic.repository.VideoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VideoLikeService {

    private final VideoLikeRepository videoLikeRepository;
    private final VideoRepository videoRepository;
    private final UserService userService;

    public VideoLikeService(VideoLikeRepository videoLikeRepository,
                            VideoRepository videoRepository,
                            UserService userService) {
        this.videoLikeRepository = videoLikeRepository;
        this.videoRepository = videoRepository;
        this.userService = userService;
    }

    private Video getVideoOrThrow(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video ne postoji"));
    }


    public long countForVideo(Long videoId) {
        return videoLikeRepository.countByVideoId(videoId);
    }


    public boolean isLiked(Long videoId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        return videoLikeRepository.existsByVideo_IdAndUser_Id(videoId, user.getId());
    }


    public long like(Long videoId, String userEmail) {
        User user = userService.findByEmail(userEmail);

        if (videoLikeRepository.existsByVideo_IdAndUser_Id(videoId, user.getId())) {
            return countForVideo(videoId);
        }

        Video video = getVideoOrThrow(videoId);
        videoLikeRepository.save(new VideoLike(video, user));
        return countForVideo(videoId);
    }


    public long unlike(Long videoId, String userEmail) {
        User user = userService.findByEmail(userEmail);

        videoLikeRepository.findByVideo_IdAndUser_Id(videoId, user.getId())
                .ifPresent(videoLikeRepository::delete);

        return countForVideo(videoId);
    }
}
