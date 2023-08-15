package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.car.Car;
import org.example.entity.car.CarBrand;
import org.example.entity.user.Lessor;
import org.example.repository.CarRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService extends BasicService<Car, Long> {

    private final CarRepository carRepository;

    @Override
    protected JpaRepository<Car, Long> getRepository() {
        return carRepository;
    }

    public List<Car> getAllByLessor(Lessor lessor) {
        return carRepository.findCarsByLessor(lessor);
    }

    public List<String> getAllNamesByLessor(Lessor lessor) {
        return getAllByLessor(lessor).stream()
                .map(Car::getFullName)
                .toList();
    }

    public Car getByFields(String carBrand, String modelName, int year, String lessorName) {

        var model = carRepository.findModelByMainFields(carBrand, modelName, year)
                .orElse(null);

        if (model == null) {
            return null;
        }

        // !!! NOT GOOD TO TAKE ALL THE RECORDS !!!
        return getAll().stream()
                .filter(car -> car.getModel().equals(model))
                .filter(car -> car.getLessor().getName().equals(lessorName))
                .findAny()
                .orElse(null);
    }

    public CarBrand getCarBrandByName(String name) {
        return carRepository.findCarBrandByName(name)
                .orElse(null);
    }

    public List<CarBrand> getAllCarBrands() {
        return carRepository.findAllCarBrands();
    }

    public List<String> getAllCarBrandNames() {
        return getAllCarBrands().stream()
                .map(CarBrand::getName)
                .toList();
    }
}
