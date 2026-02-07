package com.example.jutjubic.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "video_daily_views",
        indexes = {
                @Index(name = "idx_vdv_view_date", columnList = "view_date"),
                @Index(name = "idx_vdv_video_date", columnList = "video_id, view_date")
        },
        uniqueConstraints = {
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


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;


    @Column(name = "view_date", nullable = false)
    private LocalDate viewDate;


    @Column(name = "views_count", nullable = false)
    private long viewsCount = 0;

    protected VideoDailyViews() {}

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

    public void increment() { this.viewsCount++; }
}
