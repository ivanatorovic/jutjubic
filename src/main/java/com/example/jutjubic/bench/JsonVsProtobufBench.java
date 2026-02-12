package com.example.jutjubic.bench;

import com.example.jutjubic.messaging.UploadEventPublisher.UploadEventJson;
import com.example.jutjubic.proto.UploadEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JsonVsProtobufBench {

    public static void main(String[] args) throws Exception {
        int N = 50;
        ObjectMapper om = new ObjectMapper();

        List<Long> jsonSerNs = new ArrayList<>();
        List<Long> jsonDeNs = new ArrayList<>();
        List<Integer> jsonSize = new ArrayList<>();

        List<Long> pbSerNs = new ArrayList<>();
        List<Long> pbDeNs = new ArrayList<>();
        List<Integer> pbSize = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            UploadEventJson ev = new UploadEventJson(
                    1000L + i,
                    "Video " + i,
                    123L + i,
                    "pera",
                    LocalDateTime.now()
            );


            long t1 = System.nanoTime();
            byte[] jsonBytes = om.writeValueAsBytes(ev);
            long t2 = System.nanoTime();
            jsonSerNs.add(t2 - t1);
            jsonSize.add(jsonBytes.length);


            long t3 = System.nanoTime();
            UploadEventJson back = om.readValue(jsonBytes, UploadEventJson.class);
            long t4 = System.nanoTime();
            jsonDeNs.add(t4 - t3);


            UploadEvent pb = UploadEvent.newBuilder()
                    .setVideoId(ev.videoId() != null ? ev.videoId() : 0L)
                    .setTitle(ev.title() != null ? ev.title() : "")
                    .setSizeMB(ev.sizeMB() != null ? ev.sizeMB() : 0L)
                    .setAuthorUsername(ev.authorUsername() != null ? ev.authorUsername() : "")
                    .setCreatedAt(ev.createdAt() != null ? ev.createdAt().toString() : "")
                    .build();

            long p1 = System.nanoTime();
            byte[] pbBytes = pb.toByteArray();
            long p2 = System.nanoTime();
            pbSerNs.add(p2 - p1);
            pbSize.add(pbBytes.length);


            long p3 = System.nanoTime();
            UploadEvent backPb = UploadEvent.parseFrom(pbBytes);
            long p4 = System.nanoTime();
            pbDeNs.add(p4 - p3);


            if (back == null || backPb == null) throw new RuntimeException("impossible");
        }

        System.out.println("N = " + N);
        System.out.println();
        print("JSON", jsonSerNs, jsonDeNs, jsonSize);
        print("PROTOBUF", pbSerNs, pbDeNs, pbSize);
    }

    private static void print(String name, List<Long> serNs, List<Long> deNs, List<Integer> size) {
        double avgSerMs = avg(serNs) / 1_000_000.0;
        double avgDeMs = avg(deNs) / 1_000_000.0;
        double avgSize = avgInt(size);

        System.out.printf("%s:%n", name);
        System.out.printf("  avg serialize:   %.4f ms%n", avgSerMs);
        System.out.printf("  avg deserialize: %.4f ms%n", avgDeMs);
        System.out.printf("  avg size:        %.1f bytes%n", avgSize);
        System.out.println();
    }

    private static double avg(List<Long> xs) {
        long s = 0;
        for (long x : xs) s += x;
        return (double) s / xs.size();
    }

    private static double avgInt(List<Integer> xs) {
        long s = 0;
        for (int x : xs) s += x;
        return (double) s / xs.size();
    }
}
