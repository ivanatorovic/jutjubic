package com.example.jutjubic.messaging;

import com.example.jutjubic.config.RabbitConfig;
import com.example.jutjubic.model.TranscodeJob;
import com.example.jutjubic.model.Video;
import com.example.jutjubic.repository.TranscodeJobRepository;
import com.example.jutjubic.repository.VideoRepository;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class TranscodeConsumer {

    private final TranscodeJobRepository jobRepo;
    private final VideoRepository videoRepo;

    public TranscodeConsumer(TranscodeJobRepository jobRepo, VideoRepository videoRepo) {
        this.jobRepo = jobRepo;
        this.videoRepo = videoRepo;
    }

    @RabbitListener(queues = RabbitConfig.TRANSCODE_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handle(
            TranscodeRequestMessage msg,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long tag
    ) throws Exception {

        String instanceId = System.getenv().getOrDefault("INSTANCE_ID", "unknown");
        String consumerId = "instance-" + instanceId + "-" + Thread.currentThread().getName();
        UUID jobId = msg.jobId();

        int claimed = jobRepo.claim(
                jobId,
                consumerId,
                TranscodeJob.Status.PENDING,
                TranscodeJob.Status.PROCESSING
        );

        if (claimed == 0) {
            channel.basicAck(tag, false);
            return;
        }

        System.out.println("🟡 TRANSCODING STARTED | jobId=" + jobId + " | " + consumerId);

        try {
            File inFile = new File(msg.inputPath());
            String input = inFile.getAbsolutePath();

            File outFile = new File(
                    inFile.getParentFile(),
                    inFile.getName().replace(".mp4", "_transcoded.mp4")
            );
            String output = outFile.getAbsolutePath();

            runFfmpeg(input, output);

            TranscodeJob job = jobRepo.findById(jobId).orElseThrow();
            job.markDone();
            jobRepo.save(job);

            Video v = videoRepo.findById(msg.videoId()).orElseThrow();
            v.setTranscodedPath(output);
            v.setTranscodeStatus(Video.TranscodeStatus.READY);
            videoRepo.save(v);

            channel.basicAck(tag, false);

            System.out.println("✅ Transcoding finished | jobId=" + jobId + " | " + consumerId);

        } catch (Exception e) {
            e.printStackTrace();

            TranscodeJob job = jobRepo.findById(jobId).orElseThrow();
            job.markFailed(e.getMessage());
            jobRepo.save(job);

            Video v = videoRepo.findById(msg.videoId()).orElseThrow();
            v.setTranscodeStatus(Video.TranscodeStatus.FAILED);
            videoRepo.save(v);

            channel.basicNack(tag, false, false);
        }
    }

    private void runFfmpeg(String input, String output) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-y",
                "-hide_banner",
                "-loglevel", "info",
                "-i", input,
                "-c:v", "libx264",
                "-preset", "veryfast",
                "-crf", "23",
                "-c:a", "aac",
                output
        );

        pb.redirectErrorStream(true);
        Process p = pb.start();

        StringBuilder out = new StringBuilder();

        Thread reader = new Thread(() -> {
            try (BufferedReader r = new BufferedReader(
                    new InputStreamReader(p.getInputStream()))) {

                String line;
                while ((line = r.readLine()) != null) {
                    out.append(line).append("\n");
                }
            } catch (Exception ignored) {
            }
        });

        reader.setDaemon(true);
        reader.start();

        boolean finished = p.waitFor(120, TimeUnit.SECONDS);

        if (!finished) {
            p.destroyForcibly();
            throw new RuntimeException("FFmpeg timeout (>120s)");
        }

        int code = p.exitValue();

        if (code != 0) {
            throw new RuntimeException(
                    "FFmpeg failed, exit code=" + code + "\nOutput:\n" + out
            );
        }
    }
}
