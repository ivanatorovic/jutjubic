package com.example.jutjubic.service;

import com.example.jutjubic.model.PopularityRun;
import com.example.jutjubic.model.PopularityRunItem;
import com.example.jutjubic.repository.PopularityRunRepository;
import com.example.jutjubic.repository.VideoDailyViewsRepository;
import com.example.jutjubic.repository.VideoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PopularityEtlService {

    private final VideoDailyViewsRepository dailyRepo;
    private final VideoRepository videoRepo;
    private final PopularityRunRepository runRepo;

    public PopularityEtlService(VideoDailyViewsRepository dailyRepo,
                                VideoRepository videoRepo,
                                PopularityRunRepository runRepo) {
        this.dailyRepo = dailyRepo;
        this.videoRepo = videoRepo;
        this.runRepo = runRepo;
    }

    @Transactional
    @Scheduled(cron = "0 9 16 * * *")
    public void runDaily() {
        var rows = dailyRepo.findTop3WeightedLast7Days();
        System.out.println("ETL rows=" + rows.size());

        PopularityRun run = new PopularityRun(LocalDateTime.now());

        int rank = 1;
        for (var r : rows) {
            System.out.println("row videoId=" + r.getVideoId() + " score=" + r.getScore());

            var videoOpt = videoRepo.findById(r.getVideoId());
            System.out.println("video found=" + videoOpt.isPresent());

            if (videoOpt.isEmpty()) continue;

            long score = (r.getScore() != null) ? r.getScore() : 0L;
            run.addItem(new PopularityRunItem(rank, videoOpt.get(), score));

            rank++;
            if (rank > 3) break;
        }

        System.out.println("items before save=" + run.getItems().size());
        runRepo.save(run);
        System.out.println("saved run id=" + run.getId());
    }



    public void runNow() {
        runDaily();
    }
}
