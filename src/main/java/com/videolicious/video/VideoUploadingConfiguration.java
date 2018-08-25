package com.videolicious.video;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VideoUploadingConfiguration {

    @Value("${ffprobe.location}")
    private String ffProbeLocation;

    private ProcessedVideoRepository repository = new ProcessedVideoRepository();

    @Bean
    VideoUploader videoUploader() {
        return new FFmpegBasedVideoUploader(
                repository,
                videoProcessor()
        );
    }

    @Bean
    UploadedVideoMetadataProvider uploadedVideoMetadataProvider() {
        return repository;
    }

    private VideoProcessor videoProcessor() {
        return new VideoProcessor(ffProbeLocation);
    }

}
