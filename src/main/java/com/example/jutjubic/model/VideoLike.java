package com.example.jutjubic.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "video_likes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"video_id", "user_id"})
)
public class VideoLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public VideoLike() {}

    public VideoLike(Video video, User user) {
        this.video = video;
        this.user = user;
    }

    public Long getId() { return id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Video getVideo() { return video; }
    public void setVideo(Video video) { this.video = video; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
