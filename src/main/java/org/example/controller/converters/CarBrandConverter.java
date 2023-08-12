package org.example.controller.converters;

import lombok.RequiredArgsConstructor;
import org.example.entity.car.CarBrand;
import org.example.service.CarService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarBrandConverter implements Converter<String, CarBrand> {

    private final CarService carService;

    @Override
    public CarBrand convert(@Nullable String source) {
        return carService.getCarBrandByName(source);
    }
}
