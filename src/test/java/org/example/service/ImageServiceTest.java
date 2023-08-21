package org.example.service;

import lombok.SneakyThrows;
import org.example.TestDataProvider;
import org.example.config.TestConfig;
import org.example.repository.ImageRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

@ContextConfiguration(classes = TestConfig.class)
public class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private ImageRepository imageRepository;

    private TestDataProvider testDataProvider;

    @BeforeMethod
    public void setUp() {

        this.testDataProvider = new TestDataProvider();

        if (imageRepository == null) {
            MockitoAnnotations.openMocks(this);
        } else {
            Mockito.reset(imageRepository);
        }

        when(imageRepository.findAllByCar(null)).thenReturn(Collections.emptyList());
        when(imageRepository.saveAll(Collections.emptyList())).thenReturn(Collections.emptyList());
        when(imageRepository.saveAll(testDataProvider.getImages())).thenReturn(testDataProvider.getImages());

        for (var car : testDataProvider.getCars()) {
            when(imageRepository.findAllByCar(car)).thenReturn(car.getImages());
        }
    }

    @Test
    public void testGetAllByCar() {

        for (var car : testDataProvider.getCars()) {
            assertEquals(imageService.getAllByCar(car), car.getImages());
            verify(imageRepository, times(1)).findAllByCar(car);
        }

        assertTrue(imageService.getAllByCar(null).isEmpty());
    }

    @Test
    @SneakyThrows
    public void testAddImagesToCar() {

        int count = 0;

        for (var car : testDataProvider.getCars()) {
            List<MultipartFile> files = List.of(
                    new MockMultipartFile("test 1 name", "test 1 orig.filename", "test 1 content-type", FileInputStream.nullInputStream()),
                    new MockMultipartFile("test 2 name", "test 2 orig.filename", "test 2 content-type", FileInputStream.nullInputStream()),
                    new MockMultipartFile("test 3 name", "test 3 orig.filename", "test 3 content-type", FileInputStream.nullInputStream())
            );
            imageService.addImagesToCar(car, files);
            count++;
        }

        verify(imageRepository, times(count)).saveAll(any());
    }
}