package org.example.controller;

import org.example.TestDataProvider;
import org.example.config.TestConfig;
import org.example.service.ImageService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.*;

@ContextConfiguration(classes = TestConfig.class)
@WebAppConfiguration
public class ImageRestControllerTest {

    private MockMvc mvc;

    @Mock
    private ImageService imageService;

    private TestDataProvider testDataProvider;

    @BeforeMethod
    public void setUp() {

        testDataProvider = new TestDataProvider();

        if (imageService == null) {
            MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(imageService);
        }

        mvc = MockMvcBuilders
                .standaloneSetup(new ImageRestController(imageService))
                .build();

        when(imageService.getById(-1L)).thenReturn(null);

        for (var image : testDataProvider.getImages()) {
            when(imageService.getById(image.getId())).thenReturn(image);
        }
    }

    @Test
    public void testGetImage() throws Exception {

        mvc.perform(get("/images/{id}", -1L))
                .andExpect(status().isNotFound());

        verify(imageService, times(1)).getById(-1L);

        var images = testDataProvider.getImages();
        assertFalse(images.isEmpty());

        for (var image : images) {

            mvc.perform(get("/images/{id}", image.getId())
                            .contentType(MediaType.valueOf(image.getContentType())))
                    .andExpect(status().isOk());

            verify(imageService, times(1)).getById(image.getId());
        }
    }
}