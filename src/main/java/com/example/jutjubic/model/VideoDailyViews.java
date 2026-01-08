package com.example.jutjubic.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
        name = "video_daily_views",
        uniqueConstraints = {
                // jedan zapis po (video, datum)
                @UniqueConstraint(
                        name = "uq_video_date",
                        columnNames = {"video_id", "view_date"}
                )
        }
)
public class VideoDailyViews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // video za koji beležimo preglede
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    // datum (bez vremena!)
    @Column(name = "view_date", nullable = false)
    private LocalDate viewDate;

    // broj pregleda tog dana
    @Column(name = "views_count", nullable = false)
    private long viewsCount = 0;

    public VideoDailyViews() {}

    public VideoDailyViews(Video video, LocalDate viewDate) {
        this.video = video;
        this.viewDate = viewDate;
        this.viewsCount = 0;
    }

    public Long getId() { return id; }

    public Video getVideo() { return video; }
    public void setVideo(Video video) { this.video = video; }

    public LocalDate getViewDate() { return viewDate; }
    public void setViewDate(LocalDate viewDate) { this.viewDate = viewDate; }

    public long getViewsCount() { return viewsCount; }
    public void setViewsCount(long viewsCount) { this.viewsCount = viewsCount; }

    public void increment() {
        this.viewsCount++;
    }
}
