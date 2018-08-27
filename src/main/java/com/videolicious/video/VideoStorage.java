package com.videolicious.video;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class VideoStorage {

    private String videoStoragePath;

    VideoStorage(String videoStoragePath) {
        this.videoStoragePath = videoStoragePath;
    }

    String storeVideo(VideoProcessingJob.VideoToProcess videoToProcess) throws IOException {
        String savedVideoLocation = videoStoragePath + videoToProcess.getVideoName() + "-" + videoToProcess.getExternalVideoId();
        Files.copy(videoToProcess.getVideoContentStream(), Paths.get(savedVideoLocation));
        return savedVideoLocation;
    }

}
