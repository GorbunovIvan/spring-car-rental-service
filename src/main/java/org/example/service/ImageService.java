package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.car.Car;
import org.example.entity.car.Image;
import org.example.repository.ImageRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService extends BasicService<Image, Long> {

    private final ImageRepository imageRepository;

    @Override
    protected JpaRepository<Image, Long> getRepository() {
        return imageRepository;
    }

    public List<Image> getAllByCar(Car car) {
        return imageRepository.findAllByCar(car);
    }

    public void addImagesToCar(Car car, List<MultipartFile> files) {

        if (files.isEmpty()) {
            return;
        }

        List<Image> images = new ArrayList<>();

        for (var file : files) {
            try {
                var image = fileToImage(file);
                image.setCar(car);
                images.add(image);
            } catch (IOException e) {
                log.error("Failed to add image '" + file.getOriginalFilename() + "' to car (id=" + car.getId() + ")");
            }
        }

        imageRepository.saveAll(images);
    }

    private Image fileToImage(MultipartFile file) throws IOException {
        return Image.builder()
                .name(file.getName())
                .originalFileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .bytes(file.getBytes())
                .build();
    }
}
