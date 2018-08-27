package com.videolicious.video;

import java.io.InputStream;
import java.util.UUID;
import java.util.function.Consumer;

import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class VideoProcessingJob implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(VideoProcessingJob.class);

    private final VideoToProcess videoToProcess;
    private final VideoProcessor videoProcessor;
    private final VideoStorage videoStorage;
    private final Consumer<ProcessedVideo> postProcessingAction;

    VideoProcessingJob(VideoToProcess videoToProcess, VideoProcessor videoProcessor, VideoStorage videoStorage, Consumer<ProcessedVideo> postProcessingAction) {
        this.videoToProcess = videoToProcess;
        this.videoProcessor = videoProcessor;
        this.videoStorage = videoStorage;
        this.postProcessingAction = postProcessingAction;
    }

    @Override
    public void run() {
        try {
            String savedFileLocation = videoStorage.storeVideo(videoToProcess);
            ProcessedVideo processedVideo = videoProcessor.process(savedFileLocation, videoToProcess.externalVideoId);
            postProcessingAction.accept(processedVideo);
        } catch (Exception e) {
            log.error("Cannot save video file {}", videoToProcess.videoName, e);
            postProcessingAction.accept(ProcessedVideo.error(videoToProcess.externalVideoId));
        }
    }

    @Value
    static class VideoToProcess {

        InputStream videoContentStream;
        String videoName;
        UUID externalVideoId;
    }
}
