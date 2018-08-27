package com.videolicious.video;

import static com.videolicious.TestCommonData.FINISHED_UPLOADED_VIDEO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.videolicious.video.VideoUploader.UploadedVideoFile;
import org.awaitility.Duration;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class VideoUploaderTest {

    private TemporaryFolder temporaryFolder = new TemporaryFolder();

    private VideoUploader videoUploader;
    private UploadedVideoMetadataProvider uploadedVideoMetadataProvider;

    @Before
    public void setUp() throws IOException {
        initMocks(this);
        temporaryFolder.create();
        ProcessedVideoRepository processedVideoRepository = new ProcessedVideoRepository();
        uploadedVideoMetadataProvider = processedVideoRepository;
        MockVideoProcessor mockVideoProcessor = new MockVideoProcessor();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        VideoStorage videoStorage = new VideoStorage(temporaryFolder.newFile().getPath());
        videoUploader = new SimpleVideoUploader(processedVideoRepository, mockVideoProcessor, videoStorage, executorService);
    }

    @Test
    public void shouldCorrectlyHandleUploadedVideoAndReturnCorrespondingId() {
        // given
        UploadedVideoFile video = new UploadedVideoFile(new ByteArrayInputStream(new byte[] { 1, 2, 3 }), "file-name");

        // when
        UUID uploadedVideoId = videoUploader.upload(video);

        // then
        Optional<UploadedVideoMetadataProvider.UploadedVideo> pendingUploadedVideo = uploadedVideoMetadataProvider.metadataFor(uploadedVideoId);
        assertThat(pendingUploadedVideo).isPresent();
        assertThat(pendingUploadedVideo.get().getStatus()).isEqualTo("PENDING");

        // when
        waitForCompletion(uploadedVideoId);


        // then
        Optional<UploadedVideoMetadataProvider.UploadedVideo> finishedUploadedVideo = uploadedVideoMetadataProvider.metadataFor(uploadedVideoId);
        assertThat(finishedUploadedVideo).isPresent();
        assertThat(finishedUploadedVideo.get().getStatus()).isEqualTo("FINISHED");

    }

    @Test
    public void shouldCorrectlyReturnUUID() throws Exception {
        // given
        UploadedVideoFile video = new UploadedVideoFile(new ByteArrayInputStream(new byte[] { 1, 2, 3 }), "error-file-name");
        UUID uploadedVideoId = null;


        // when
        try {
            uploadedVideoId = videoUploader.upload(video);
            waitForCompletion(uploadedVideoId);
        } catch (Exception e) {
            //do nothing exception must be catched
        }
//
        // then
        Optional<UploadedVideoMetadataProvider.UploadedVideo> errorUploadedVideo = uploadedVideoMetadataProvider.metadataFor(uploadedVideoId);
        assertThat(errorUploadedVideo).isPresent();
        assertThat(errorUploadedVideo.get().getStatus()).isEqualTo("ERROR");
    }

    private void waitForCompletion(UUID uploadedVideoId) {
        await().ignoreExceptions().atMost(Duration.FIVE_SECONDS).until(
                () -> !uploadedVideoMetadataProvider.metadataFor(uploadedVideoId).get().getStatus().equals("PENDING")
        );
    }

    private class MockVideoProcessor implements VideoProcessor {

        @Override
        public ProcessedVideo process(String videoToProcessLocation, UUID id) {
            if (videoToProcessLocation.contains("error-file-name")) {
                throw new RuntimeException();
            }
            return new ProcessedVideo(id, FINISHED_UPLOADED_VIDEO.getVideoMetadata(), videoToProcessLocation, ProcessedVideo.Status.FINISHED);
        }
    }

}