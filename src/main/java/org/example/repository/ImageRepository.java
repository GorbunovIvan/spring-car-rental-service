package org.example.repository;

import org.example.entity.car.Car;
import org.example.entity.car.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findAllByCar(Car car);
}
