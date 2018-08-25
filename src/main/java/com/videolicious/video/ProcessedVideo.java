package com.videolicious.video;

import java.util.UUID;

import com.videolicious.video.UploadedVideoMetadataProvider.VideoMetadata;
import lombok.Value;

@Value
class ProcessedVideo {

    private UUID id;
    private VideoMetadata videoMetadata;
    private String processedVideoLocation;
    private Status status;


    static ProcessedVideo pending(UUID id) {
        return new ProcessedVideo(id, null, null, Status.PENDING);
    }

    static ProcessedVideo error(UUID id) {
        return new ProcessedVideo(id, null, null, Status.ERROR);
    }

    static ProcessedVideo finished(UUID id, VideoMetadata metadata, String processedVideoLocation) {
        return new ProcessedVideo(id, metadata, processedVideoLocation, Status.FINISHED);
    }

    enum Status {
        PENDING, ERROR, FINISHED
    }

}
