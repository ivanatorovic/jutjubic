package com.example.jutjubic.util;

import java.nio.file.Path;

public class FfprobeUtil {

    public static int probeDurationSeconds(Path file) {
        try {
            Process p = new ProcessBuilder(
                    "ffprobe", "-v", "error",
                    "-show_entries", "format=duration",
                    "-of", "default=noprint_wrappers=1:nokey=1",
                    file.toAbsolutePath().toString()
            ).redirectErrorStream(true).start();

            String out = new String(p.getInputStream().readAllBytes()).trim();
            int code = p.waitFor();
            if (code != 0 || out.isBlank()) return 0;

            double seconds = Double.parseDouble(out);
            return (int) Math.ceil(seconds);
        } catch (Exception e) {
            return 0;
        }
    }
}
