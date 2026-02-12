package com.example.jutjubic.controller;

import com.example.jutjubic.dto.MqBenchResult;
import com.example.jutjubic.messaging.UploadEventPublisher;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mq")
public class MqBenchController {

    private final UploadEventPublisher publisher;

    public MqBenchController(UploadEventPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping("/bench")
    public MqBenchResult bench(@RequestParam(defaultValue = "50") int count) {
        if (count < 1) count = 1;
        if (count > 5000) count = 5000;

        return publisher.benchmark(count);
    }
}
