package com.videolicious;

import java.util.UUID;

import com.videolicious.video.UploadedVideoMetadataProvider;

public class TestCommonData {
    
    public static final UUID CORRECTLY_UPLOADED_VIDEO_ID = UUID.randomUUID();
    public static final UUID FINISHED_UPLOADED_VIDEO_ID = CORRECTLY_UPLOADED_VIDEO_ID;
    public static final UUID VIDEO_IN_PENDING_STATUS = UUID.randomUUID();
    public static final UUID VIDEO_THAT_CAUSES_ERROR = UUID.randomUUID();

    public static final UploadedVideoMetadataProvider.UploadedVideo PENDING_UPLOADED_VIDEO = new UploadedVideoMetadataProvider.UploadedVideo(
            "PENDING",
            null
    );

    public static final UploadedVideoMetadataProvider.UploadedVideo ERROR_UPLOADED_VIDEO = new UploadedVideoMetadataProvider.UploadedVideo(
            "ERROR",
            null
    );
    public static final UploadedVideoMetadataProvider.UploadedVideo FINISHED_UPLOADED_VIDEO =
            new UploadedVideoMetadataProvider.UploadedVideo(
                    "FINISHED",
                    UploadedVideoMetadataProvider.VideoMetadata.VideoMetadataBuilder.metatdata()
                            .duration(1020.12)
                            .videoSize("12mb")
                            .audioBitRate(12L)
                            .audioCodec("codec")
                            .videoBitRate(12L)
                            .videoCodec("codec")
                            .create());


}
