package com.videolicious;

import com.videolicious.vide.UploadedVideoHandler;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableAutoConfiguration
public class VideoFileInspectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(VideoFileInspectorApplication.class, args);
    }

    @Bean
    UploadedVideoHandler uploadedVideoHandler() {
        return new UploadedVideoHandler() {

            private UUID uuid = UUID.randomUUID();

            @Override
            public UUID upload(VideoFile videoFile) {
                return uuid;
            }

            @Override
            public Optional<VideoMetadata> metadataFor(UUID uploadedVideoId) {
                return uploadedVideoId.equals(uuid) ? Optional.of(metatdata()) : Optional.empty();
            }

            private VideoMetadata metatdata() {
                return VideoMetadata.metadata()
                    .duration("10sec")
                    .videoSize("12mb")
                    .audioBitRate(12)
                    .audioCodec("codec")
                    .videoBitRate(12)
                    .videoCodec("codec")
                    .build();
            }
        };
    }
}
