package org.example.controller.converters;

import lombok.RequiredArgsConstructor;
import org.example.entity.car.Car;
import org.example.service.CarService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class CarConverter implements Converter<String, Car> {

    private final CarService carService;

    @Override
    public Car convert(@NonNull String source) {

        var pattern = Pattern.compile("(\\S+) (\\S+), (\\d{4}) by (\\S+)");
        var matcher = pattern.matcher(source);

        if (!matcher.find()) {
            return null;
        }

        String carBrand = matcher.group(1).replaceAll(" ", "-");
        String modelName = matcher.group(2).replaceAll(" ", "-");
        int year = Integer.parseInt(matcher.group(3));
        String lessorName = matcher.group(4);

        Car car = carService.getByFields(carBrand, modelName, year, lessorName);

        if (car == null) {
            throw new RuntimeException(String.format("Car not found - %s %s, %d by %s",
                    carBrand,
                    modelName,
                    year,
                    lessorName));
        }

        return car;
    }
}
