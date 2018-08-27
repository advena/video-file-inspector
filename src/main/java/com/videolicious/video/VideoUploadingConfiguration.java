package com.videolicious.video;

import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VideoUploadingConfiguration {

    @Value("${ffprobe.location}")
    private String ffProbeLocation;

    @Value("${processed.video.location")
    private String processedVideoLocation;

    private ProcessedVideoRepository repository = new ProcessedVideoRepository();

    @Bean
    VideoUploader videoUploader() {
        return new SimpleVideoUploader(
                repository,
                videoProcessor(),
                new VideoStorage(processedVideoLocation),
                Executors.newSingleThreadExecutor());
    }

    @Bean
    UploadedVideoMetadataProvider uploadedVideoMetadataProvider() {
        return repository;
    }

    private VideoProcessor videoProcessor() {
        return new VideoProcessor.FfmpegBasedVideoProcessor(ffProbeLocation);
    }

}
