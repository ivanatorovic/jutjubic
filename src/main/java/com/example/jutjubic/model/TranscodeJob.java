package com.example.jutjubic.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transcode_jobs")
public class TranscodeJob {

    public enum Status {
        PENDING,
        PROCESSING,
        DONE,
        FAILED
    }

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
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    public TranscodeJob() {}

    public TranscodeJob(UUID jobId, Video video, String inputPath) {
        this.jobId = jobId;
        this.video = video;
        this.inputPath = inputPath;
        this.status = Status.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void markProcessing(String consumerId) {
        this.status = Status.PROCESSING;
        this.consumerId = consumerId;
        this.startedAt = LocalDateTime.now();
        this.errorMessage = null;
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
    public void setJobId(UUID jobId) { this.jobId = jobId; }

    public Video getVideo() { return video; }
    public void setVideo(Video video) { this.video = video; }

    public String getInputPath() { return inputPath; }
    public void setInputPath(String inputPath) { this.inputPath = inputPath; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getConsumerId() { return consumerId; }
    public void setConsumerId(String consumerId) { this.consumerId = consumerId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getFinishedAt() { return finishedAt; }
    public void setFinishedAt(LocalDateTime finishedAt) { this.finishedAt = finishedAt; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
