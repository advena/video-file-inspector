package com.videolicious.vide;

import java.util.Optional;
import java.util.UUID;

class FFmpegBasedUploadedVideoHandler implements UploadedVideoHandler {

    @Override
    public UUID upload(VideoFile videoFile) {
        UUID fileId = UUID.randomUUID();
        //create video processing job
        //save uuid with initial status
        //run job
        //return uuid
        return null;
    }

    @Override
    public Optional<VideoMetadata> metadataFor(UUID uploadedVideoId) {
        return Optional.empty();
    }
}
