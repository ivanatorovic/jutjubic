package com.example.jutjubic;

import com.example.jutjubic.model.Video;
import com.example.jutjubic.repository.VideoRepository;
import com.example.jutjubic.service.VideoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VideoViewCounterServiceTests {

    @Autowired private VideoService videoService;
    @Autowired private VideoRepository videoRepository;

    private Long videoId;

    @BeforeAll
    void setUp() {
        Video v = new Video();
        v.setTitle("Test naslov");
        v.setDescription("Test opis");
        v.setTags(List.of("test"));

        v = videoRepository.saveAndFlush(v);
        videoId = v.getId();
    }

    @Test
    void shouldIncrementViewsWithConcurrentVisits() throws Exception {
        int users = 50;
        int visitsPerUser = 10;
        int expected = users * visitsPerUser;

        ExecutorService pool = Executors.newFixedThreadPool(users);

        CountDownLatch ready = new CountDownLatch(users);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(users);

        for (int i = 0; i < users; i++) {
            pool.submit(() -> {
                ready.countDown();
                try {
                    start.await();
                    for (int j = 0; j < visitsPerUser; j++) {

                        videoService.getDtoById(videoId);
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

        Video updated = videoRepository.findById(videoId).orElseThrow();
        Assertions.assertEquals(expected, updated.getViewCount());
    }
}
