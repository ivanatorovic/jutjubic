package com.example.jutjubic.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(
        name = "popularity_run_items",
        indexes = {
                @Index(name = "idx_pri_run", columnList = "run_id"),
                @Index(name = "idx_pri_video", columnList = "video_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_run_rank", columnNames = {"run_id", "rank"}),
                @UniqueConstraint(name = "uq_run_video", columnNames = {"run_id", "video_id"})
        }
)
public class PopularityRunItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "run_id", nullable = false)
    @JsonBackReference
    private PopularityRun run;

    @Column(name = "rank", nullable = false)
    private int rank;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(name = "score", nullable = false)
    private long score;

    protected PopularityRunItem() {}

    public PopularityRunItem(int rank, Video video, long score) {
        this.rank = rank;
        this.video = video;
        this.score = score;
    }

    public Long getId() { return id; }

    public PopularityRun getRun() { return run; }
    public void setRun(PopularityRun run) { this.run = run; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public Video getVideo() { return video; }
    public void setVideo(Video video) { this.video = video; }

    public long getScore() { return score; }
    public void setScore(long score) { this.score = score; }
}
