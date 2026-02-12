package com.example.jutjubic.messaging;

import com.example.jutjubic.config.RabbitConfig;
import com.example.jutjubic.dto.MqBenchResult;
import com.example.jutjubic.model.Video;
import com.example.jutjubic.proto.UploadEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;

@Service
public class UploadEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void publishBothAfterCommit(Video v) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publishBoth(v);
                }
            });
        } else {

            publishBoth(v);
        }
    }

    public UploadEventPublisher(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishBoth(Video savedVideo) {
        publishJson(savedVideo);
        publishProtobuf(savedVideo);
    }

    public void publishJson(Video v) {
        UploadEventJson json = new UploadEventJson(
                v.getId(),
                v.getTitle(),
                v.getSizeMB(),
                v.getUser() != null ? v.getUser().getUsername() : null,
                v.getCreatedAt()
        );

        try {
            byte[] jsonPayload = objectMapper.writeValueAsBytes(json);

            MessageProperties props = new MessageProperties();
            props.setContentType("application/json");
            props.setContentEncoding("UTF-8");

            rabbitTemplate.send(
                    RabbitConfig.UPLOAD_EXCHANGE,
                    RabbitConfig.UPLOAD_ROUTING_JSON,
                    new Message(jsonPayload, props)
            );
        } catch (Exception e) {
            throw new RuntimeException("JSON serialize failed", e);
        }
    }

    public void publishProtobuf(Video v) {
        String createdAt = (v.getCreatedAt() != null) ? v.getCreatedAt().toString() : "";

        UploadEvent pb = UploadEvent.newBuilder()
                .setVideoId(v.getId() != null ? v.getId() : 0L)
                .setTitle(v.getTitle() != null ? v.getTitle() : "")
                .setSizeMB(v.getSizeMB() != null ? v.getSizeMB() : 0L)
                .setAuthorUsername(
                        v.getUser() != null && v.getUser().getUsername() != null ? v.getUser().getUsername() : ""
                )
                .setCreatedAt(createdAt)
                .build();

        byte[] payload = pb.toByteArray();

        MessageProperties props = new MessageProperties();
        props.setContentType("application/x-protobuf");
        props.setContentEncoding("binary");

        rabbitTemplate.send(
                RabbitConfig.UPLOAD_EXCHANGE,
                RabbitConfig.UPLOAD_ROUTING_PB,
                new Message(payload, props)
        );
    }

    public record UploadEventJson(
            Long videoId,
            String title,
            Long sizeMB,
            String authorUsername,
            LocalDateTime createdAt
    ) {}

    public MqBenchResult benchmark(int count) {

        long jsonSerNanos = 0;
        long pbSerNanos = 0;

        long jsonBytesSum = 0;
        long pbBytesSum = 0;

        MessageProperties jsonProps = new MessageProperties();
        jsonProps.setContentType("application/json");
        jsonProps.setContentEncoding("UTF-8");

        for (int i = 0; i < count; i++) {

            long videoId = 1000L + i;
            String title = "bench-" + i;
            long sizeMb = 35L + (i % 10);
            String author = "zika";
            LocalDateTime createdAt = LocalDateTime.now();


            UploadEventJson jsonObj = new UploadEventJson(videoId, title, sizeMb, author, createdAt);

            long t1 = System.nanoTime();
            byte[] jsonPayload;
            try {
                jsonPayload = objectMapper.writeValueAsBytes(jsonObj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            long t2 = System.nanoTime();

            jsonSerNanos += (t2 - t1);
            jsonBytesSum += jsonPayload.length;


            rabbitTemplate.send(
                    RabbitConfig.UPLOAD_EXCHANGE,
                    RabbitConfig.UPLOAD_ROUTING_JSON,
                    new Message(jsonPayload, jsonProps)
            );


            UploadEvent pb = UploadEvent.newBuilder()
                    .setVideoId(videoId)
                    .setTitle(title)
                    .setSizeMB(sizeMb)
                    .setAuthorUsername(author)
                    .setCreatedAt(createdAt.toString())
                    .build();

            long p1 = System.nanoTime();
            byte[] pbPayload = pb.toByteArray();
            long p2 = System.nanoTime();

            pbSerNanos += (p2 - p1);
            pbBytesSum += pbPayload.length;

            publishProtobufBytes(pbPayload);
        }

        double avgJsonMicros = (jsonSerNanos / 1000.0) / count;
        double avgPbMicros = (pbSerNanos / 1000.0) / count;

        return new MqBenchResult(
                count,
                avgJsonMicros,
                avgPbMicros,
                (double) jsonBytesSum / count,
                (double) pbBytesSum / count
        );
    }

    private void publishProtobufBytes(byte[] payload) {
        MessageProperties props = new MessageProperties();
        props.setContentType("application/x-protobuf");
        props.setContentEncoding("binary");

        rabbitTemplate.send(
                RabbitConfig.UPLOAD_EXCHANGE,
                RabbitConfig.UPLOAD_ROUTING_PB,
                new Message(payload, props)
        );
    }
}
