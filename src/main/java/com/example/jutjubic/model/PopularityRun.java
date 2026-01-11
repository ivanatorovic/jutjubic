package com.example.jutjubic.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "popularity_runs")
public class PopularityRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "run_at", nullable = false)
    private LocalDateTime runAt = LocalDateTime.now();

    @JsonIgnore
    @OneToMany(mappedBy = "run", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("rank ASC")
    private List<PopularityRunItem> items = new ArrayList<>();

    public PopularityRun() {}

    public PopularityRun(LocalDateTime runAt) {
        this.runAt = runAt;
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
