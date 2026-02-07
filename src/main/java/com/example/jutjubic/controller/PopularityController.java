package com.example.jutjubic.controller;

import com.example.jutjubic.dto.PopularBlockDto;
import com.example.jutjubic.dto.PopularVideoDto;
import com.example.jutjubic.repository.PopularityRunRepository;
import com.example.jutjubic.service.PopularityEtlService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;

@RestController
@RequestMapping("/api/popularity")
public class PopularityController {

    private final PopularityRunRepository runRepo;
    private final PopularityEtlService etl;

    public PopularityController(PopularityRunRepository runRepo, PopularityEtlService etl) {
        this.runRepo = runRepo;
        this.etl = etl;
    }

    @GetMapping("/latest")
    public PopularBlockDto latest() {
        var run = runRepo.findLatestOne(PageRequest.of(0,1))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ETL još nije pokrenut"));

        var top3 = run.getItems().stream()
                .sorted(Comparator.comparingInt(i -> i.getRank()))
                .map(i -> new PopularVideoDto(
                        i.getVideo().getId(),
                        i.getVideo().getTitle(),
                        i.getVideo().getThumbnailPath(),
                        i.getScore()
                ))
                .toList();

        return new PopularBlockDto(run.getRunAt(), top3);
    }


    @PostMapping("/run-now")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void runNow() {
        etl.runNow();
    }
}
