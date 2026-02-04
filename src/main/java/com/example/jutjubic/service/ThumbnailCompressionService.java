package com.example.jutjubic.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class ThumbnailCompressionService {

    public void compressToJpeg(File input, File output, double quality) throws Exception {
        Thumbnails.of(input)
                .scale(1.0)
                .outputFormat("jpg")
                .outputQuality(quality)
                .toFile(output);
    }
}