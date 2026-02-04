package com.example.jutjubic.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "transcode_jobs",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_transcode_job_video", columnNames = {"video_id"})
        },
        indexes = {
                @Index(name = "idx_transcode_status", columnList = "status"),
                @Index(name = "idx_transcode_video", columnList = "video_id")
        }
)
public class TranscodeJob {

    public enum Status { PENDING, PROCESSING, DONE, FAILED }

    @Id
    @Column(name = "job_id", nullable = false, updatable = false)
    private UUID jobId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(name = "input_path", nullable = false, length = 500)
    private String inputPath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.PENDING;

    @Column(name = "consumer_id", length = 120)
    private String consumerId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    protected TranscodeJob() {}

    public TranscodeJob(Video video, String inputPath) {
        this.video = video;
        this.inputPath = inputPath;
        this.status = Status.PENDING;
    }

    @PrePersist
    void prePersist() {
        if (this.jobId == null) this.jobId = UUID.randomUUID();
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = Status.PENDING;
    }

    public void markProcessing(String consumerId) {
        this.status = Status.PROCESSING;
        this.consumerId = consumerId;
        this.startedAt = LocalDateTime.now();
        this.errorMessage = null;
        this.finishedAt = null;
    }

    public void markDone() {
        this.status = Status.DONE;
        this.finishedAt = LocalDateTime.now();
    }

    public void markFailed(String msg) {
        this.status = Status.FAILED;
        this.errorMessage = msg;
        this.finishedAt = LocalDateTime.now();
    }


    public UUID getJobId() { return jobId; }
    public Video getVideo() { return video; }
    public String getInputPath() { return inputPath; }
    public Status getStatus() { return status; }
    public String getConsumerId() { return consumerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public String getErrorMessage() { return errorMessage; }

    public void setVideo(Video video) { this.video = video; }
    public void setInputPath(String inputPath) { this.inputPath = inputPath; }
}
