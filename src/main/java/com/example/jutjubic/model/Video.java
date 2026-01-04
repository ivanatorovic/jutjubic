package com.example.jutjubic.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "videos") // ime tabele u bazi
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                     // primarni ključ

    @Column(nullable = false)
    private String title;                // naslov videa

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;          // opis videa

    @ElementCollection
    @CollectionTable(name = "video_tags", joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "tag")
    private List<String> tags;           // lista tagova

    @Column(name = "thumbnail_path")
    private String thumbnailPath;        // putanja do thumbnail slike na disku

    @Column(name = "video_path")
    private String videoPath;            // putanja do video fajla na disku

    @Column(name = "size_mb")
    private Long sizeMB;                 // veličina videa u MB

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now(); // vreme kreiranja objave

    private String location;             // opcionalna geolokacija

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


    // ------------------ KONSTRUKTORI ------------------

    public Video() {} // obavezan prazan konstruktor za JPA

    public Video(String title, String description, List<String> tags,
                 String thumbnailPath, String videoPath, Long sizeMB, String location) {
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.thumbnailPath = thumbnailPath;
        this.videoPath = videoPath;
        this.sizeMB = sizeMB;
        this.location = location;
    }

    // ------------------ GETTERI I SETTERI ------------------

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
}
