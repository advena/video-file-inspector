package com.videolicious.video.web;

import static com.videolicious.video.web.VideoUploadingController.VIDEO_URL;

import com.videolicious.vide.UploadedVideoHandler;
import com.videolicious.vide.UploadedVideoHandler.VideoFile;
import com.videolicious.vide.UploadedVideoHandler.VideoMetadata;
import java.io.IOException;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(VIDEO_URL)
class VideoUploadingController {

    static final String VIDEO_URL = "/video";

    private final UploadedVideoHandler uploadedVideoHandler;

    VideoUploadingController(UploadedVideoHandler uploadedVideoHandler) {
        this.uploadedVideoHandler = uploadedVideoHandler;
    }

    @PutMapping
    ResponseEntity<UUID> uploadVideo(@RequestParam MultipartFile videoFile, UriComponentsBuilder uriBuilder) throws IOException {
        videoFile.getInputStream();
        UUID uploadedVideoId = uploadedVideoHandler.upload(new VideoFile(videoFile.getInputStream(), videoFile.getName()));
        return ResponseEntity.created(
            uriBuilder.path(VIDEO_URL + "/")
                .path(uploadedVideoId.toString())
                .build()
                .toUri()
        ).build();
    }

    @GetMapping(value = "/{uploadedVideoId}/metadata")
    ResponseEntity<VideoMetadata> getMetadataFor(@PathVariable String uploadedVideoId) {
        return uploadedVideoHandler.metadataFor(UUID.fromString(uploadedVideoId))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

}
