package com.videolicious.video;

import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

class FFmpegBasedVideoUploader implements VideoUploader {

    private ProcessedVideoRepository repository;
    private VideoProcessor videoProcessor;

    FFmpegBasedVideoUploader(ProcessedVideoRepository repository, VideoProcessor videoProcessor) {
        this.repository = repository;
        this.videoProcessor = videoProcessor;
    }

    @Override
    public UUID upload(UploadedVideoFile uploadedVideoFile) {
        UUID fileId = UUID.randomUUID();
        processVideo(uploadedVideoFile, fileId);
        repository.save(ProcessedVideo.pending(fileId));
        return fileId;
    }

    private void processVideo(UploadedVideoFile uploadedVideoFile, UUID fileId) {
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        VideoProcessingJob videoProcessingJob = prepareVideoProcessingJob(fileId, uploadedVideoFile);
        forkJoinPool.execute(videoProcessingJob);
    }

    private VideoProcessingJob prepareVideoProcessingJob(UUID fileId, UploadedVideoFile uploadedVideoFile) {
        return new VideoProcessingJob(
                new VideoProcessingJob.VideoToProcess(
                        uploadedVideoFile.getInputStream(),
                        uploadedVideoFile.getName(),
                        fileId
                ),
                videoProcessor,
                processedVideo -> repository.save(processedVideo)
        );
    }
}
