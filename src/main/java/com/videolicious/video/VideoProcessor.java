package com.videolicious.video;

import static com.videolicious.video.UploadedVideoMetadataProvider.VideoMetadata.VideoMetadataBuilder.metatdata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import com.videolicious.video.UploadedVideoMetadataProvider.VideoMetadata;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.probe.FFmpegStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

interface VideoProcessor {

    ProcessedVideo process(String videoToProcessLocation, UUID id);

    class FfmpegBasedVideoProcessor implements VideoProcessor {

        private static final Logger log = LoggerFactory.getLogger(VideoProcessor.class);

        private final String ffProbeLocation;

        FfmpegBasedVideoProcessor(String ffProbeLocation) {
            this.ffProbeLocation = ffProbeLocation;
        }

        @Override
        public ProcessedVideo process(String videoToProcessLocation, UUID id) {
            try {
                log.info("Processing video file with id {}", id);
                FFprobe fFprobe = new FFprobe(ffProbeLocation);
                FFmpegProbeResult result = fFprobe.probe(videoToProcessLocation);
                log.info("Extracting processed video {} metadata", id);
                VideoMetadata videoMetadata = prepareVideoMetadata(result, videoToProcessLocation);
                log.info("Processing video {} finished", id);
                return ProcessedVideo.finished(id, videoMetadata, videoToProcessLocation);
            } catch (IOException e) {
                log.error("Processing video with id {} stored at {} results with exception", id, videoToProcessLocation, e);
                return ProcessedVideo.error(id);
            }
        }

        private VideoMetadata prepareVideoMetadata(FFmpegProbeResult result, String videoToProcessLocation) throws IOException {
            VideoMetadata.VideoMetadataBuilder metadataBuilder = metatdata();
            long fileSize = Files.size(Paths.get(videoToProcessLocation));
            metadataBuilder.videoSize(FileUtils.byteCountToDisplaySize(fileSize));
            result.getStreams()
                    .forEach(
                            fFmpegStream -> {
                                if (audioStream(fFmpegStream)) {
                                    handleAudioStream(fFmpegStream, metadataBuilder);
                                }
                                if (videoStream(fFmpegStream)) {
                                    handleVideoStream(fFmpegStream, metadataBuilder);
                                }
                            }
                    );
            return metadataBuilder.create();
        }

        private void handleVideoStream(FFmpegStream fFmpegStream, VideoMetadata.VideoMetadataBuilder metadataBuilder) {
            metadataBuilder.videoBitRate(fFmpegStream.bit_rate);
            metadataBuilder.videoCodec(fFmpegStream.codec_long_name);
            metadataBuilder.duration(fFmpegStream.duration);
        }

        private boolean videoStream(FFmpegStream fFmpegStream) {
            return FFmpegStream.CodecType.VIDEO == fFmpegStream.codec_type;
        }

        private void handleAudioStream(FFmpegStream fFmpegStream, VideoMetadata.VideoMetadataBuilder metadataBuilder) {
            metadataBuilder.audioBitRate(fFmpegStream.bit_rate);
            metadataBuilder.audioCodec(fFmpegStream.codec_long_name);
        }

        private boolean audioStream(FFmpegStream stream) {
            return FFmpegStream.CodecType.AUDIO == stream.codec_type;
        }

    }
}

