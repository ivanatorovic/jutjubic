package com.example.jutjubic.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "videos")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "video_tags", joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(name = "thumbnail_path")
    private String thumbnailPath;

    @Column(name = "video_path")
    private String videoPath;

    @Column(name = "size_mb")
    private Long sizeMB;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "location")
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VideoLike> likes = new ArrayList<>();

    @Column(name = "view_count", nullable = false)
    private long viewCount = 0;

    @Column(name = "scheduled", nullable = false)
    private boolean scheduled = false;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "thumbnail_compressed_path")
    private String thumbnailCompressedPath;


    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "geohash", length = 12)
    private String geohash;

    @Column(name = "premiere_ended", nullable = false)
    private boolean premiereEnded = false;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;


    public Video() {}

    public Video(String title,
                 String description,
                 List<String> tags,
                 String thumbnailPath,
                 String videoPath,
                 Long sizeMB,
                 String location,
                 boolean scheduled,
                 LocalDateTime scheduledAt,
                 User user,
                 Double latitude,
                 Double longitude) {

        this.title = title;
        this.description = description;
        this.tags = tags;
        this.thumbnailPath = thumbnailPath;
        this.videoPath = videoPath;
        this.sizeMB = sizeMB;
        this.location = location;
        this.scheduled = scheduled;
        this.scheduledAt = scheduledAt;
        this.user = user;
    }



    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }

    public long getViewCount() {
        return viewCount;
    }

    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public List<String> getTags() { return tags; }

    public Double getLatitude(){
        return latitude;
    }
    public Double getLongitude(){
        return longitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setTags(List<String> tags) { this.tags = tags; }

    public String getThumbnailPath() { return thumbnailPath; }

    public void setThumbnailPath(String thumbnailPath) { this.thumbnailPath = thumbnailPath; }

    public String getVideoPath() { return videoPath; }

    public void setVideoPath(String videoPath) { this.videoPath = videoPath; }

    public Long getSizeMB() { return sizeMB; }

    public void setSizeMB(Long sizeMB) { this.sizeMB = sizeMB; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getLocation() { return location; }

    public void setLocation(String location) { this.location = location; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public String getGeohash() { return geohash; }

    public void setGeohash(String geohash) { this.geohash = geohash; }

    public boolean isScheduled() { return scheduled; }
    public void setScheduled(boolean scheduled) { this.scheduled = scheduled; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public boolean isPremiereEnded() {
        return premiereEnded;
    }

    public void setPremiereEnded(boolean premiereEnded) {
        this.premiereEnded = premiereEnded;
    }

    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }
}
