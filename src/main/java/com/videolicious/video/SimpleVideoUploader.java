package com.videolicious.video;

import java.util.UUID;
import java.util.concurrent.ExecutorService;

class SimpleVideoUploader implements VideoUploader {

    private final ProcessedVideoRepository repository;
    private final VideoProcessor videoProcessor;
    private final VideoStorage videoStorage;
    private final ExecutorService executorService;

    SimpleVideoUploader(ProcessedVideoRepository repository, VideoProcessor videoProcessor, VideoStorage videoStorage, ExecutorService executorService) {
        this.repository = repository;
        this.videoProcessor = videoProcessor;
        this.videoStorage = videoStorage;
        this.executorService = executorService;
    }

    @Override
    public UUID upload(UploadedVideoFile uploadedVideoFile) {
        UUID fileId = UUID.randomUUID();
        processVideo(uploadedVideoFile, fileId);
        repository.save(ProcessedVideo.pending(fileId));
        return fileId;
    }

    private void processVideo(UploadedVideoFile uploadedVideoFile, UUID fileId) {
        VideoProcessingJob videoProcessingJob = prepareVideoProcessingJob(fileId, uploadedVideoFile);
        executorService.submit(videoProcessingJob);
    }

    private VideoProcessingJob prepareVideoProcessingJob(UUID fileId, UploadedVideoFile uploadedVideoFile) {
        return new VideoProcessingJob(
                new VideoProcessingJob.VideoToProcess(
                        uploadedVideoFile.getInputStream(),
                        uploadedVideoFile.getName(),
                        fileId
                ),
                videoProcessor,
                videoStorage, repository::save
        );
    }
}
