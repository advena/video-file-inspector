package com.videolicious.video.web;

import com.videolicious.video.UploadedVideoMetadataProvider;
import com.videolicious.video.VideoUploader;
import com.videolicious.video.VideoUploader.UploadedVideoFile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;

import static com.videolicious.video.web.VideoUploadingController.VIDEO_URL;

@RestController
@RequestMapping(VIDEO_URL)
class VideoUploadingController {

    static final String VIDEO_URL = "/video";

    private final VideoUploader videoUploader;
    private final UploadedVideoMetadataProvider uploadedVideoMetadataProvider;

    VideoUploadingController(VideoUploader videoUploader, UploadedVideoMetadataProvider uploadedVideoMetadataProvider) {
        this.videoUploader = videoUploader;
        this.uploadedVideoMetadataProvider = uploadedVideoMetadataProvider;
    }

    //fixme investigate how to send request with content-type via curl or postman and remove @Ignore at desired test method
//    @PutMapping(consumes = "video/*")
    @PutMapping
    ResponseEntity<UUID> uploadVideo(@RequestParam MultipartFile videoFile, UriComponentsBuilder uriBuilder) throws IOException {
        videoFile.getInputStream();
        UUID uploadedVideoId = videoUploader.upload(new UploadedVideoFile(videoFile.getInputStream(), videoFile.getName()));
        return ResponseEntity.created(
                uriBuilder.path(VIDEO_URL + "/")
                        .path(uploadedVideoId.toString())
                        .path("/metadata")
                        .build()
                        .toUri()
        ).build();
    }

    @GetMapping(value = "/{uploadedVideoId}/metadata")
    ResponseEntity<UploadedVideoMetadataProvider.UploadedVideo> getMetadataFor(@PathVariable String uploadedVideoId) {
        return uploadedVideoMetadataProvider.metadataFor(UUID.fromString(uploadedVideoId))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
