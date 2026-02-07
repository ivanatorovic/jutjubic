package com.example.jutjubic.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "popularity_runs",
        indexes = {
                @Index(name = "idx_pop_run_at", columnList = "run_at")
        }
)
public class PopularityRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "run_at", nullable = false)
    private LocalDateTime runAt;

    @OneToMany(mappedBy = "run", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("rank ASC")
    @JsonManagedReference
    private List<PopularityRunItem> items = new ArrayList<>();

    protected PopularityRun() {}

    public PopularityRun(LocalDateTime runAt) {
        this.runAt = runAt;
    }

    @PrePersist
    void prePersist() {
        if (runAt == null) runAt = LocalDateTime.now();
    }

    public void addItem(PopularityRunItem item) {
        items.add(item);
        item.setRun(this);
    }

    public void removeItem(PopularityRunItem item) {
        items.remove(item);
        item.setRun(null);
    }

    public Long getId() { return id; }

    public LocalDateTime getRunAt() { return runAt; }
    public void setRunAt(LocalDateTime runAt) { this.runAt = runAt; }

    public List<PopularityRunItem> getItems() { return items; }
    public void setItems(List<PopularityRunItem> items) { this.items = items; }
}
