package com.videolicious.video;

import java.util.Optional;
import java.util.UUID;

import lombok.Value;

public interface UploadedVideoMetadataProvider {

    Optional<UploadedVideo> metadataFor(UUID uploadedVideoId);

    @Value
    class UploadedVideo {

        String status;
        VideoMetadata videoMetadata;
    }

    @Value
    class VideoMetadata {

        Double duration;
        String videoSize;
        Long videoBitRate;
        String videoCodec;
        Long audioBitRate;
        String audioCodec;

        static public class VideoMetadataBuilder {

            private Double duration;
            private String videoSize;
            private Long videoBitRate;
            private String videoCodec;
            private Long audioBitRate;
            private String audioCodec;

            public static VideoMetadataBuilder metatdata() {
                return new VideoMetadataBuilder();
            }

            public VideoMetadataBuilder duration(Double duration) {
                this.duration = duration;
                return this;
            }

            public VideoMetadataBuilder videoSize(String videoSize) {
                this.videoSize = videoSize;
                return this;
            }

            public VideoMetadataBuilder videoBitRate(Long videoBitRate) {
                this.videoBitRate = videoBitRate;
                return this;
            }

            public VideoMetadataBuilder videoCodec(String videoCodec) {
                this.videoCodec = videoCodec;
                return this;
            }

            public VideoMetadataBuilder audioBitRate(Long audioBitRate) {
                this.audioBitRate = audioBitRate;
                return this;
            }

            public VideoMetadataBuilder audioCodec(String audioCodec) {
                this.audioCodec = audioCodec;
                return this;
            }

            public UploadedVideoMetadataProvider.VideoMetadata create() {
                return new UploadedVideoMetadataProvider.VideoMetadata(duration, videoSize, videoBitRate, videoCodec, audioBitRate, audioCodec);
            }
        }
    }

}
