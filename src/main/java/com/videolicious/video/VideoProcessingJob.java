package com.videolicious.video;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.function.Consumer;

import lombok.Builder;
import lombok.Value;

class VideoProcessingJob implements Runnable{

    private final VideoToProcess videoToProcess;
    private final VideoProcessor videoProcessor;
    private final Consumer<ProcessedVideo> postProcessingAction;

    VideoProcessingJob(VideoToProcess videoToProcess, VideoProcessor videoProcessor, Consumer<ProcessedVideo> postProcessingAction) {
        this.videoToProcess = videoToProcess;
        this.videoProcessor = videoProcessor;
        this.postProcessingAction = postProcessingAction;
    }

    @Override
    public void run() {
        try {
            String savedFileLocation = saveFile();
            ProcessedVideo processedVideo = videoProcessor.process(savedFileLocation, videoToProcess.externalVideoId);
            postProcessingAction.accept(processedVideo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String saveFile() throws IOException {
        String savedVideoLocation = "/tmp/" + videoToProcess.videoName + videoToProcess.externalVideoId;
        Files.copy(videoToProcess.videoContentStream, Paths.get(savedVideoLocation));
        return savedVideoLocation;
    }

    @Value
    @Builder(builderMethodName = "video")
    static class VideoToProcess {

        InputStream videoContentStream;
        String videoName;
        UUID externalVideoId;
    }
}
