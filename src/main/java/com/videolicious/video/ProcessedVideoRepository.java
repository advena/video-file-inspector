package com.videolicious.video;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

class ProcessedVideoRepository implements UploadedVideoMetadataProvider {

    private Map<UUID, ProcessedVideo> DATABASE = new HashMap<>();

    void save(ProcessedVideo video) {
        DATABASE.put(video.getId(), video);
    }

    @Override
    public Optional<UploadedVideo> metadataFor(UUID uploadedVideoId) {
        return Optional.of(DATABASE.get(uploadedVideoId))
                .map(processedVideo -> new UploadedVideo(
                        processedVideo.getStatus().name(),
                        processedVideo.getVideoMetadata()
                ));
    }
}
