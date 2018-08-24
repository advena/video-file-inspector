package com.videolicious.vide;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;

public interface UploadedVideoHandler {

    UUID upload(VideoFile videoFile);

    Optional<VideoMetadata> metadataFor(UUID uploadedVideoId);

    @Value
    class VideoFile {

        InputStream inputStream;
        String name;

    }

    @Value
    @Builder(
        builderMethodName = "metadata"
    )
    class VideoMetadata {

        String duration;
        String videoSize;
        Integer videoBitRate;
        String videoCodec;
        Integer audioBitRate;
        String audioCodec;
    }

}
