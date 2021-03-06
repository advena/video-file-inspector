package com.videolicious.video.web;

import com.videolicious.video.UploadedVideoMetadataProvider;
import com.videolicious.video.VideoUploader;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static com.videolicious.TestCommonData.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(VideoUploadingController.class)
public class VideoUploadingControllerTest {

    private static final MockMultipartFile CORRECT_VIDEO_FILE =
            new MockMultipartFile("videoFile", "name.mpg", "video/mp4", ofSize(1));


    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideoUploader videoUploader;

    @MockBean
    private UploadedVideoMetadataProvider uploadedVideoMetadataProvider;

    @Before
    public void setUpMock() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        when(videoUploader.upload(any())).thenReturn(CORRECTLY_UPLOADED_VIDEO_ID);
        when(uploadedVideoMetadataProvider.metadataFor(any())).thenReturn(Optional.empty());
        when(uploadedVideoMetadataProvider.metadataFor(CORRECTLY_UPLOADED_VIDEO_ID)).thenReturn(Optional.of(FINISHED_UPLOADED_VIDEO));
        when(uploadedVideoMetadataProvider.metadataFor(VIDEO_IN_PENDING_STATUS)).thenReturn(Optional.of(PENDING_UPLOADED_VIDEO));
        when(uploadedVideoMetadataProvider.metadataFor(VIDEO_THAT_CAUSES_ERROR)).thenReturn(Optional.of(ERROR_UPLOADED_VIDEO));
    }

    @Test
    public void shouldReturnCorrectStatusWithResourceLocationForCorrectVideoFile() throws Exception {
        mockMvc.perform(fileUploadBuilder("/video")
                .file(CORRECT_VIDEO_FILE)
                .contentType("video/mp4"))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("**/video/" + CORRECTLY_UPLOADED_VIDEO_ID.toString() + "/metadata"));
    }

    //fixme
    @Test
    @Ignore
    public void shouldNotAllowToSendRequestWithIncorrectContentType() throws Exception {
        mockMvc.perform(fileUploadBuilder("/video")
                .file(CORRECT_VIDEO_FILE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnsupportedMediaType());
    }


    @Test
    public void shouldReturnUploadedVideoMetadataWithFinishedStatus() throws Exception {
        mockMvc.perform(get("/video/" + FINISHED_UPLOADED_VIDEO_ID + "/metadata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("FINISHED")))
                .andExpect(jsonPath("$.videoMetadata.duration", is(1020.12)))
                .andExpect(jsonPath("$.videoMetadata.videoSize", is("12mb")))
                .andExpect(jsonPath("$.videoMetadata.videoBitRate", is(12)))
                .andExpect(jsonPath("$.videoMetadata.videoCodec", is("codec")))
                .andExpect(jsonPath("$.videoMetadata.audioBitRate", is(12)))
                .andExpect(jsonPath("$.videoMetadata.audioCodec", is("codec")));

    }

    @Test
    public void shouldReturnPendingStatusOnStillProcessingVideo() throws Exception {
        mockMvc.perform(get("/video/" + VIDEO_IN_PENDING_STATUS + "/metadata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.videoMetadata", nullValue()));
    }

    @Test
    public void shouldReturnErrorStatusOnErrorInVideoProcessing() throws Exception {
        mockMvc.perform(get("/video/" + VIDEO_THAT_CAUSES_ERROR + "/metadata"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ERROR")))
                .andExpect(jsonPath("$.videoMetadata", nullValue()));
    }

    @Test
    public void shouldReturn404OnNonExistingResourceCall() throws Exception {
        mockMvc.perform(get("/video/" + UUID.randomUUID().toString() + "/metadata"))
                .andExpect(status().isNotFound());
    }

    //fixme check why spring.servlet.multipart.max-file-size is not used in MockMvc
    @Test
    @Ignore
    public void shouldNotAllowToUploadFileThatHasExcitedMaxFileUploadSize() throws Exception {
        MockMultipartFile file = new MockMultipartFile("videoFile", "name.mpg", "video/mp4", ofSize(1000));
        mockMvc.perform(fileUploadBuilder("/video")
                .file(file))
                .andExpect(status().is5xxServerError());
    }

    //for overriding default POST method as mutlipartfile uploader
    private MockMultipartHttpServletRequestBuilder fileUploadBuilder(String url) {
        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.fileUpload(url);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });
        return builder;
    }

    private static byte[] ofSize(int size) {
        byte oneByte = 0x1;
        byte[] content = new byte[size * 1024 * 1024];
        Arrays.fill(content, oneByte);

        return content;
    }

}