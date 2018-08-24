package com.videolicious.video.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.videolicious.vide.UploadedVideoHandler;
import com.videolicious.vide.UploadedVideoHandler.VideoMetadata;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@WebMvcTest(VideoUploadingController.class)
public class VideoUploadingControllerTest {

    private static final MockMultipartFile CORRECT_VIDEO_FILE =
        new MockMultipartFile("videoFile", "name.mpg", "video/mp4", ofSize(1));

    private static final UUID CORRECTLY_UPLOADED_VIDEO_ID = UUID.randomUUID();
    private static final VideoMetadata UPLOADED_VIDEO_METADATA = VideoMetadata.metadata()
        .duration("10sec")
        .videoSize("12mb")
        .audioBitRate(12)
        .audioCodec("codec")
        .videoBitRate(12)
        .videoCodec("codec")
        .build();

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UploadedVideoHandler uploadedVideoHandler;

    @Before
    public void setUpMock() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        when(uploadedVideoHandler.upload(any())).thenReturn(CORRECTLY_UPLOADED_VIDEO_ID);
        when(uploadedVideoHandler.metadataFor(any())).thenReturn(Optional.empty());
        when(uploadedVideoHandler.metadataFor(CORRECTLY_UPLOADED_VIDEO_ID)).thenReturn(Optional.of(UPLOADED_VIDEO_METADATA));
    }

    @Test
    public void shouldReturnCorrectStatusWithResourceLocationForCorrectVideoFile() throws Exception {
        mockMvc.perform(fileUploadBuilder("/video")
            .file(CORRECT_VIDEO_FILE))
            .andExpect(status().isCreated())
            .andExpect(redirectedUrlPattern("**/video/" + CORRECTLY_UPLOADED_VIDEO_ID.toString()));
    }

    @Test
    public void shouldReturnUploadedVideoMetadataOnExistingResourceCall() throws Exception {
        mockMvc.perform(get("/video/" + CORRECTLY_UPLOADED_VIDEO_ID + "/metadata"))
            .andExpect(status().isOk());
        //todo add body check
    }

    @Test
    public void shouldReturn404OnNonExistingResourceCall() throws Exception {
        mockMvc.perform(get("/video/" + UUID.randomUUID().toString() + "/metadata"))
            .andExpect(status().isNotFound());
    }




    public void shouldNotAllowToUploadFileThatHasExcitedMaxFileUploadSize() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile("videoFile", "name.mpg", "video/mp4", ofSize(100000000));
        mockMvc.perform(fileUploadBuilder("/video")
            .file(file))
            .andExpect(status().is5xxServerError());
        // when

        // then
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