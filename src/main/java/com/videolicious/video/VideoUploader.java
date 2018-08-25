package com.videolicious.video;

import java.io.InputStream;
import java.util.UUID;

import lombok.Value;

public interface VideoUploader {

    UUID upload(UploadedVideoFile uploadedVideoFile);

    @Value
    class UploadedVideoFile {

        InputStream inputStream;
        String name;

    }
}
