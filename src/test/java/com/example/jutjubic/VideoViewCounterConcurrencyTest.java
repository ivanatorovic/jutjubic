package com.example.jutjubic;

import com.example.jutjubic.model.Video;
import com.example.jutjubic.repository.VideoRepository;
import com.example.jutjubic.service.VideoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
class VideoViewCounterConcurrencyTest {

    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoRepository videoRepository;

    @Test
    void shouldIncrementViewCountCorrectlyWithConcurrentVisits() throws Exception {
        // 1) napravi video koji prolazi NOT NULL constraint-e
        Video v = new Video();
        v.setTitle("Test naslov");
        v.setDescription("Test opis");
        v.setTags(List.of("test"));
        v = videoRepository.saveAndFlush(v);

        Long videoId = v.getId();

        // 2) simulacija "istovremene posete"
        int users = 50; // broj paralelnih korisnika
        int visitsPerUser = 10; // koliko puta svaki korisnik "udje na stranicu"
        int expected = users * visitsPerUser;

        ExecutorService pool = Executors.newFixedThreadPool(users);

        CountDownLatch ready = new CountDownLatch(users);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(users);

        for (int i = 0; i < users; i++) {
            pool.submit(() -> {
                ready.countDown();
                try {
                    start.await(); // svi krenu u isto vreme
                    for (int j = 0; j < visitsPerUser; j++) {
                        videoService.registerView(videoId); // +1 view (atomski UPDATE)
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        ready.await();
        start.countDown();
        done.await();
        pool.shutdown();

        // 3) proveri da nema "izgubljenih" inkremenata
        Video updated = videoRepository.findById(videoId).orElseThrow();
        Assertions.assertEquals(expected, updated.getViewCount());
    }
}
