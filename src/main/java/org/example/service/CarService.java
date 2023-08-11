package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.car.Car;
import org.example.repository.CarRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CarService extends BasicService<Car, Long> {

    private final CarRepository carRepository;

    @Override
    protected JpaRepository<Car, Long> getRepository() {
        return carRepository;
    }
}
